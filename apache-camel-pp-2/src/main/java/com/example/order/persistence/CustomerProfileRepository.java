package com.example.order.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfileEntity, UUID> {

    Optional<CustomerProfileEntity> findByCustomerId(String customerId);
}
