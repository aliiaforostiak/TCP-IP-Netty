package com.example.order.processor;

import com.example.order.domain.DeadLetterEvent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class DlqNotificationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        DeadLetterEvent event = exchange.getMessage().getBody(DeadLetterEvent.class);
        exchange.getMessage().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(event);
    }
}
