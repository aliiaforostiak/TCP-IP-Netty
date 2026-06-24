package com.example.order.processor;

import com.example.order.domain.OrderCreated;
import com.example.order.domain.OrderItem;
import com.example.order.service.OrderValidationException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderValidationProcessorTest {

    private final OrderValidationProcessor processor = new OrderValidationProcessor();

    @Test
    void validatesCorrectOrder() {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setMessage(new DefaultMessage(camelContext));
        exchange.getMessage().setBody(validOrder());

        assertDoesNotThrow(() -> processor.process(exchange));
    }

    @Test
    void rejectsMismatchedTotal() {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setMessage(new DefaultMessage(camelContext));
        exchange.getMessage().setBody(new OrderCreated(
                "order-2",
                "cust-1001",
                List.of(new OrderItem("sku-1", "Item", 2, new BigDecimal("10.00"))),
                new BigDecimal("15.00"),
                "USD",
                Instant.parse("2026-06-23T10:00:00Z"),
                null
        ));

        assertThrows(OrderValidationException.class, () -> processor.process(exchange));
    }

    private OrderCreated validOrder() {
        return new OrderCreated(
                "order-1",
                "cust-1001",
                List.of(new OrderItem("sku-1", "Widget", 2, new BigDecimal("10.00"))),
                new BigDecimal("20.00"),
                "USD",
                Instant.parse("2026-06-23T10:00:00Z"),
                "SUMMER7"
        );
    }
}
