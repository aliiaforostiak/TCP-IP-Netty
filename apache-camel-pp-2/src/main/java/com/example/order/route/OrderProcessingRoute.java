package com.example.order.route;

import com.example.order.domain.OrderCreated;
import com.example.order.processor.DlqNotificationProcessor;
import com.example.order.processor.OrderEnrichmentProcessor;
import com.example.order.processor.OrderPersistenceProcessor;
import com.example.order.processor.OrderPricingProcessor;
import com.example.order.processor.OrderValidationProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessingRoute extends RouteBuilder {

    private final OrderValidationProcessor orderValidationProcessor;
    private final OrderEnrichmentProcessor orderEnrichmentProcessor;
    private final OrderPricingProcessor orderPricingProcessor;
    private final OrderPersistenceProcessor orderPersistenceProcessor;
    private final DlqNotificationProcessor dlqNotificationProcessor;

    public OrderProcessingRoute(OrderValidationProcessor orderValidationProcessor,
                                OrderEnrichmentProcessor orderEnrichmentProcessor,
                                OrderPricingProcessor orderPricingProcessor,
                                OrderPersistenceProcessor orderPersistenceProcessor,
                                DlqNotificationProcessor dlqNotificationProcessor) {
        this.orderValidationProcessor = orderValidationProcessor;
        this.orderEnrichmentProcessor = orderEnrichmentProcessor;
        this.orderPricingProcessor = orderPricingProcessor;
        this.orderPersistenceProcessor = orderPersistenceProcessor;
        this.dlqNotificationProcessor = dlqNotificationProcessor;
    }

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .useOriginalMessage()
                .process(dlqNotificationProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:{{app.kafka.orders-dlq-topic}}");

        from("kafka:{{app.kafka.orders-in-topic}}"
                + "?groupId=orders-demo"
        )
                .routeId("order-ingest-route")
                .unmarshal().json(JsonLibrary.Jackson, OrderCreated.class)
                .setProperty("orderStage").constant("VALIDATION")
                .process(orderValidationProcessor)
                .setProperty("orderStage").constant("ENRICHMENT")
                .process(orderEnrichmentProcessor)
                .setProperty("orderStage").constant("PRICING")
                .process(orderPricingProcessor)
                .setProperty("orderStage").constant("PERSISTENCE")
                .process(orderPersistenceProcessor)
                .wireTap("seda:order-notifications")
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:{{app.kafka.orders-processed-topic}}");
    }
}
