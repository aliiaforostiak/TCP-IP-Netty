package com.example.order.processor;

import com.example.order.domain.OrderProcessedEvent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class OrderBillingNotificationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        OrderProcessedEvent event = exchange.getMessage().getBody(OrderProcessedEvent.class);
        exchange.getMessage().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(event);
    }
}
