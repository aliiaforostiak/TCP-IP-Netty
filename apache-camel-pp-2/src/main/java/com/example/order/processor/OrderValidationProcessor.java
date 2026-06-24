package com.example.order.processor;

import com.example.order.domain.OrderCreated;
import com.example.order.domain.OrderItem;
import com.example.order.service.OrderValidationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Component
public class OrderValidationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        OrderCreated order = exchange.getMessage().getBody(OrderCreated.class);
        if (order == null) {
            throw new OrderValidationException("Order payload is missing");
        }
        if (isBlank(order.orderId())) {
            throw new OrderValidationException("orderId is required");
        }
        if (isBlank(order.customerId())) {
            throw new OrderValidationException("customerId is required");
        }
        if (isBlank(order.currency())) {
            throw new OrderValidationException("currency is required");
        }
        if (order.createdAt() == null) {
            throw new OrderValidationException("createdAt is required");
        }
        if (order.items().isEmpty()) {
            throw new OrderValidationException("items must not be empty");
        }
        if (order.items().stream().anyMatch(this::invalidItem)) {
            throw new OrderValidationException("each item must have sku, name, positive quantity and positive unitPrice");
        }

        BigDecimal calculatedTotal = order.items().stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (order.totalAmount() == null) {
            throw new OrderValidationException("totalAmount is required");
        }
        if (order.totalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("totalAmount must be positive");
        }
        if (calculatedTotal.compareTo(order.totalAmount()) != 0) {
            throw new OrderValidationException(
                    "totalAmount mismatch: expected " + calculatedTotal + " but got " + order.totalAmount());
        }

        exchange.setProperty("orderId", order.orderId());
    }

    private boolean invalidItem(OrderItem item) {
        return item == null
                || isBlank(item.sku())
                || isBlank(item.name())
                || item.quantity() <= 0
                || item.unitPrice() == null
                || item.unitPrice().compareTo(BigDecimal.ZERO) <= 0
                || Objects.isNull(item.lineTotal());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
