package org.example.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class DataBaseProcessor implements Processor {
    @Override
    public void process(Exchange exchange){
        System.out.println("Database processor for: " + exchange.getMessage().getBody());
    }
}
