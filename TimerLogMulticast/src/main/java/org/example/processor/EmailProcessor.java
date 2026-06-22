package org.example.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class EmailProcessor implements Processor {
    @Override
    public void process(Exchange exchange){
        System.out.println("Email processor for: " + exchange.getMessage().getBody());
    }
}
