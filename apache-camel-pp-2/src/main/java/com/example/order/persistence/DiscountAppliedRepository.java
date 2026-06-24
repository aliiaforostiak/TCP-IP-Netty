package com.example.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiscountAppliedRepository extends JpaRepository<DiscountAppliedEntity, UUID> {
}
