package com.example.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

public record OrderCreated(
        String orderId,
        String customerId,
        List<OrderItem> items,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        String promoCode
) {

    public OrderCreated {
        items = items == null ? List.of() : List.copyOf(items);
        if (totalAmount != null) {
            totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
