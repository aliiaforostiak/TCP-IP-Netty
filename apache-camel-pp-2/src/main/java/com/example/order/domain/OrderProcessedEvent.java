package com.example.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

public record OrderProcessedEvent(
        String orderId,
        String customerId,
        String currency,
        BigDecimal grossAmount,
        BigDecimal discountAmount,
        BigDecimal netAmount,
        List<AppliedDiscount> discountsApplied,
        Instant processedAt,
        String status
) {

    public OrderProcessedEvent {
        discountsApplied = discountsApplied == null ? List.of() : List.copyOf(discountsApplied);
        if (grossAmount != null) {
            grossAmount = grossAmount.setScale(2, RoundingMode.HALF_UP);
        }
        if (discountAmount != null) {
            discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        }
        if (netAmount != null) {
            netAmount = netAmount.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
