package com.example.order.domain;

import java.time.Instant;

public record DeadLetterEvent(
        String orderId,
        String stage,
        String reason,
        String payload,
        Instant failedAt
) {
}
