package com.example.order.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processing_audit")
public class ProcessingAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false, length = 32)
    private String stage;

    @Column(nullable = false, length = 32)
    private String outcome;

    @Column(nullable = false, length = 512)
    private String details;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ProcessingAuditEntity() {
    }

    public ProcessingAuditEntity(String orderId, String stage, String outcome, String details, Instant createdAt) {
        this.orderId = orderId;
        this.stage = stage;
        this.outcome = outcome;
        this.details = details;
        this.createdAt = createdAt;
    }
}
