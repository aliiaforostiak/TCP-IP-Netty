package com.example.order.route;

import com.example.order.processor.OrderBillingNotificationProcessor;
import com.example.order.processor.OrderEmailNotificationProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationRoute extends RouteBuilder {

    private final OrderBillingNotificationProcessor billingNotificationProcessor;
    private final OrderEmailNotificationProcessor emailNotificationProcessor;

    public OrderNotificationRoute(OrderBillingNotificationProcessor billingNotificationProcessor,
                                  OrderEmailNotificationProcessor emailNotificationProcessor) {
        this.billingNotificationProcessor = billingNotificationProcessor;
        this.emailNotificationProcessor = emailNotificationProcessor;
    }

    @Override
    public void configure() {
        from("seda:order-notifications")
                .routeId("order-notification-route")
                .multicast().stopOnException()
                .to("direct:billing-notification")
                .to("direct:email-notification");

        from("direct:billing-notification")
                .routeId("order-billing-notification-route")
                .process(billingNotificationProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .toD("{{app.integration.billing-url}}?bridgeEndpoint=true&throwExceptionOnFailure=false");

        from("direct:email-notification")
                .routeId("order-email-notification-route")
                .process(emailNotificationProcessor)
                .to("smtp://{{app.mail.host}}:{{app.mail.port}}?from={{app.mail.from}}&to={{app.integration.notification-email}}");
    }
}
