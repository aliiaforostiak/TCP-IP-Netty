package com.example.order.processor;

import com.example.order.domain.OrderProcessedEvent;
import org.apache.camel.Exchange;
import org.apache.camel.component.mail.MailConstants;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class OrderEmailNotificationProcessor implements Processor {

    private final String notificationEmail;
    private final String mailFrom;

    public OrderEmailNotificationProcessor(
            @Value("${app.integration.notification-email}") String notificationEmail,
            @Value("${app.mail.from}") String mailFrom) {
        this.notificationEmail = notificationEmail;
        this.mailFrom = mailFrom;
    }

    @Override
    public void process(Exchange exchange) {
        OrderProcessedEvent event = exchange.getMessage().getBody(OrderProcessedEvent.class);
        String subject = "Order " + event.orderId() + " processed";
        exchange.getMessage().setHeader("To", notificationEmail);
        exchange.getMessage().setHeader("From", mailFrom);
        exchange.getMessage().setHeader("Subject", subject);
        exchange.getMessage().setHeader(MailConstants.MAIL_TO, notificationEmail);
        exchange.getMessage().setHeader(MailConstants.MAIL_FROM, mailFrom);
        exchange.getMessage().setHeader(MailConstants.MAIL_SUBJECT, subject);
        exchange.getMessage().setBody(render(event));
    }

    private String render(OrderProcessedEvent event) {
        return """
                Order processed successfully
                Order: %s
                Customer: %s
                Gross: %s %s
                Discount: %s %s
                Net: %s %s
                Discounts: %s
                """.formatted(
                event.orderId(),
                event.customerId(),
                event.grossAmount(),
                event.currency().toUpperCase(Locale.ROOT),
                event.discountAmount(),
                event.currency().toUpperCase(Locale.ROOT),
                event.netAmount(),
                event.currency().toUpperCase(Locale.ROOT),
                event.discountsApplied()
        );
    }
}
