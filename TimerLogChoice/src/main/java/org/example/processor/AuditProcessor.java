package org.example.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class AuditProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Integer body = exchange.getMessage().getBody(Integer.class);
        System.out.println("Got body: " + body);
        Integer newBody = body * 2;
        exchange.getMessage().setBody(newBody);
        System.out.println("Processed body to: " + newBody);
        exchange.getMessage().getHeaders().put("processed", "true");
    }
}
