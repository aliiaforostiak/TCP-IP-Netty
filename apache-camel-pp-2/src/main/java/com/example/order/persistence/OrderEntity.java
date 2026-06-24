package com.example.order.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Column(nullable = false, length = 16)
    private String currency;

    @Column(name = "gross_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "net_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmount;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    @Column(name = "promo_code", length = 64)
    private String promoCode;

    public OrderEntity() {
    }

    public OrderEntity(String orderId, String customerId, String currency, BigDecimal grossAmount, BigDecimal discountAmount, BigDecimal netAmount, String status, Instant createdAt, Instant processedAt, String promoCode) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.currency = currency;
        this.grossAmount = grossAmount;
        this.discountAmount = discountAmount;
        this.netAmount = netAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.promoCode = promoCode;
    }

    public String getOrderId() {
        return orderId;
    }
}
