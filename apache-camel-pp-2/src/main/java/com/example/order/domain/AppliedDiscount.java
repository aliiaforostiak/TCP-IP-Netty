package com.example.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record AppliedDiscount(DiscountType type, String reason, BigDecimal amount) {

    public AppliedDiscount {
        if (amount != null) {
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
