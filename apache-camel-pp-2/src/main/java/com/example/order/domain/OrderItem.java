package com.example.order.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record OrderItem(String sku, String name, int quantity, BigDecimal unitPrice) {

    public OrderItem {
        if (unitPrice != null) {
            unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
