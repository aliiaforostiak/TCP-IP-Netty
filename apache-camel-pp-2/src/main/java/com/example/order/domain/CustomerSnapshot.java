package com.example.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CustomerSnapshot(
        String customerId,
        CustomerStatus status,
        CustomerSegment segment,
        long completedOrders,
        BigDecimal lifetimeValue
) {

    public CustomerSnapshot {
        if (lifetimeValue != null) {
            lifetimeValue = lifetimeValue.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
