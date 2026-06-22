package org.example.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class KafkaProcessor implements Processor {
    @Override
    public void process(Exchange exchange){
        System.out.println("Kafka processor for: " + exchange.getMessage().getBody());
    }
}
