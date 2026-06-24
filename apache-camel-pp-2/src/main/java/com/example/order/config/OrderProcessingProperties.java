package com.example.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app.processing")
public record OrderProcessingProperties(
        BigDecimal firstOrderDiscountPercent,
        BigDecimal loyaltyDiscountPercent,
        BigDecimal promoCodeDiscountPercent,
        BigDecimal bulkDiscountPercent,
        Integer bulkQuantityThreshold
) {
}
