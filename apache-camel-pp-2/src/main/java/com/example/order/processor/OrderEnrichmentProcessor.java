package com.example.order.processor;

import com.example.order.domain.OrderCreated;
import com.example.order.domain.OrderPipelineContext;
import com.example.order.service.OrderProcessingService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class OrderEnrichmentProcessor implements Processor {

    private final OrderProcessingService orderProcessingService;

    public OrderEnrichmentProcessor(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @Override
    public void process(Exchange exchange) {
        OrderCreated order = exchange.getMessage().getBody(OrderCreated.class);
        OrderPipelineContext context = orderProcessingService.enrich(order);
        exchange.getMessage().setBody(context);
    }
}
