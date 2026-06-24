package com.example.order.persistence;

import com.example.order.domain.CustomerSegment;
import com.example.order.domain.CustomerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false, unique = true, length = 64)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CustomerStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CustomerSegment segment;

    @Column(name = "completed_orders", nullable = false)
    private long completedOrders;

    @Column(name = "lifetime_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal lifetimeValue;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CustomerProfileEntity() {
    }

    public CustomerProfileEntity(String customerId, CustomerStatus status, CustomerSegment segment, long completedOrders, BigDecimal lifetimeValue, Instant updatedAt) {
        this.customerId = customerId;
        this.status = status;
        this.segment = segment;
        this.completedOrders = completedOrders;
        this.lifetimeValue = lifetimeValue;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public CustomerSegment getSegment() {
        return segment;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public BigDecimal getLifetimeValue() {
        return lifetimeValue;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
