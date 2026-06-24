package com.example.order.processor;

import com.example.order.domain.OrderPipelineContext;
import com.example.order.service.OrderProcessingService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class OrderPersistenceProcessor implements Processor {

    private final OrderProcessingService orderProcessingService;

    public OrderPersistenceProcessor(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @Override
    public void process(Exchange exchange) {
        OrderPipelineContext context = exchange.getMessage().getBody(OrderPipelineContext.class);
        exchange.getMessage().setBody(orderProcessingService.persist(context));
    }
}
