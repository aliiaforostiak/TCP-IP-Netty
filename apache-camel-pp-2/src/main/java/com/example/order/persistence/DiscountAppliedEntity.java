package com.example.order.persistence;

import com.example.order.domain.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "discounts_applied")
public class DiscountAppliedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DiscountType type;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    public DiscountAppliedEntity() {
    }

    public DiscountAppliedEntity(String orderId, DiscountType type, String reason, BigDecimal amount) {
        this.orderId = orderId;
        this.type = type;
        this.reason = reason;
        this.amount = amount;
    }
}
