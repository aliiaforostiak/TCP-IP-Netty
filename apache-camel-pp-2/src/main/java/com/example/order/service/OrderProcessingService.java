package com.example.order.service;

import com.example.order.config.OrderProcessingProperties;
import com.example.order.domain.AppliedDiscount;
import com.example.order.domain.CustomerSegment;
import com.example.order.domain.CustomerSnapshot;
import com.example.order.domain.CustomerStatus;
import com.example.order.domain.DeadLetterEvent;
import com.example.order.domain.DiscountType;
import com.example.order.domain.OrderCreated;
import com.example.order.domain.OrderItem;
import com.example.order.domain.OrderPipelineContext;
import com.example.order.domain.OrderProcessedEvent;
import com.example.order.persistence.CustomerProfileEntity;
import com.example.order.persistence.CustomerProfileRepository;
import com.example.order.persistence.DiscountAppliedEntity;
import com.example.order.persistence.DiscountAppliedRepository;
import com.example.order.persistence.OrderEntity;
import com.example.order.persistence.OrderItemEntity;
import com.example.order.persistence.OrderItemRepository;
import com.example.order.persistence.OrderRepository;
import com.example.order.persistence.ProcessingAuditEntity;
import com.example.order.persistence.ProcessingAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderProcessingService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final OrderProcessingProperties properties;
    private final CustomerProfileRepository customerProfileRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiscountAppliedRepository discountAppliedRepository;
    private final ProcessingAuditRepository processingAuditRepository;

    public OrderProcessingService(OrderProcessingProperties properties,
                                  CustomerProfileRepository customerProfileRepository,
                                  OrderRepository orderRepository,
                                  OrderItemRepository orderItemRepository,
                                  DiscountAppliedRepository discountAppliedRepository,
                                  ProcessingAuditRepository processingAuditRepository) {
        this.properties = properties;
        this.customerProfileRepository = customerProfileRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.discountAppliedRepository = discountAppliedRepository;
        this.processingAuditRepository = processingAuditRepository;
    }

    public CustomerSnapshot loadCustomerSnapshot(String customerId) {
        CustomerProfileEntity entity = customerProfileRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        return new CustomerSnapshot(
                entity.getCustomerId(),
                entity.getStatus(),
                entity.getSegment(),
                entity.getCompletedOrders(),
                entity.getLifetimeValue()
        );
    }

    public OrderPipelineContext enrich(OrderCreated order) {
        CustomerSnapshot customer = loadCustomerSnapshot(order.customerId());
        if (customer.status() != CustomerStatus.ACTIVE) {
            throw new OrderValidationException("Customer is not active: " + customer.customerId());
        }
        return new OrderPipelineContext(order, customer, order.totalAmount(), List.of(), BigDecimal.ZERO, order.totalAmount());
    }

    public OrderPipelineContext price(OrderPipelineContext context) {
        OrderCreated order = context.order();
        CustomerSnapshot customer = context.customer();
        BigDecimal gross = context.grossAmount();
        List<AppliedDiscount> discounts = new ArrayList<>();

        if (customer.completedOrders() == 0) {
            discounts.add(percentDiscount(DiscountType.FIRST_ORDER, "First completed order", gross, properties.firstOrderDiscountPercent()));
        }
        if (customer.segment() == CustomerSegment.GOLD || customer.segment() == CustomerSegment.VIP || customer.completedOrders() >= 10) {
            discounts.add(percentDiscount(DiscountType.LOYALTY, "Loyalty customer", gross, properties.loyaltyDiscountPercent()));
        }
        if (order.promoCode() != null && !order.promoCode().isBlank()) {
            discounts.add(percentDiscount(DiscountType.PROMO_CODE, "Promo code " + order.promoCode(), gross, properties.promoCodeDiscountPercent()));
        }
        int bulkThreshold = properties.bulkQuantityThreshold() == null ? 10 : properties.bulkQuantityThreshold();
        boolean hasBulkItem = order.items().stream().anyMatch(item -> item.quantity() >= bulkThreshold);
        if (hasBulkItem) {
            discounts.add(percentDiscount(DiscountType.BULK, "Bulk item quantity threshold reached", gross, properties.bulkDiscountPercent()));
        }

        BigDecimal discountAmount = discounts.stream()
                .map(AppliedDiscount::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (discountAmount.compareTo(gross) > 0) {
            discountAmount = gross;
        }

        BigDecimal net = gross.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        return new OrderPipelineContext(order, customer, gross, discounts, discountAmount, net);
    }

    @Transactional
    public OrderProcessedEvent persist(OrderPipelineContext context) {
        OrderCreated order = context.order();

        orderRepository.save(new OrderEntity(
                order.orderId(),
                order.customerId(),
                order.currency(),
                context.grossAmount(),
                context.discountAmount(),
                context.netAmount(),
                "PROCESSED",
                order.createdAt(),
                Instant.now(),
                order.promoCode()
        ));

        for (OrderItem item : order.items()) {
            orderItemRepository.save(new OrderItemEntity(
                    order.orderId(),
                    item.sku(),
                    item.name(),
                    item.quantity(),
                    item.unitPrice(),
                    item.lineTotal()
            ));
        }

        for (AppliedDiscount discount : context.discounts()) {
            discountAppliedRepository.save(new DiscountAppliedEntity(
                    order.orderId(),
                    discount.type(),
                    discount.reason(),
                    discount.amount()
            ));
        }

        processingAuditRepository.save(new ProcessingAuditEntity(
                order.orderId(),
                "PERSISTENCE",
                "SUCCESS",
                "Order persisted with " + context.discounts().size() + " discount(s)",
                Instant.now()
        ));

        return new OrderProcessedEvent(
                order.orderId(),
                order.customerId(),
                order.currency(),
                context.grossAmount(),
                context.discountAmount(),
                context.netAmount(),
                context.discounts(),
                Instant.now(),
                "PROCESSED"
        );
    }

    @Transactional
    public DeadLetterEvent recordFailure(String orderId, String stage, String reason, String payload) {
        processingAuditRepository.save(new ProcessingAuditEntity(
                orderId == null ? "unknown" : orderId,
                stage,
                "FAILED",
                reason,
                Instant.now()
        ));
        return new DeadLetterEvent(orderId, stage, reason, payload, Instant.now());
    }

    private AppliedDiscount percentDiscount(DiscountType type, String reason, BigDecimal gross, BigDecimal percent) {
        BigDecimal amount = gross
                .multiply(percent)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
        return new AppliedDiscount(type, reason, amount);
    }
}
