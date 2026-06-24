package com.example.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record OrderPipelineContext(
        OrderCreated order,
        CustomerSnapshot customer,
        BigDecimal grossAmount,
        List<AppliedDiscount> discounts,
        BigDecimal discountAmount,
        BigDecimal netAmount
) {

    public OrderPipelineContext {
        discounts = discounts == null ? List.of() : List.copyOf(discounts);
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
