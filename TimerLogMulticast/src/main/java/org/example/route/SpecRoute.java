package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import org.example.processor.DataBaseProcessor;
import org.example.processor.EmailProcessor;
import org.example.processor.KafkaProcessor;
import org.springframework.stereotype.Component;

@Component
public class SpecRoute extends RouteBuilder {

    private final EmailProcessor emailProcessor;
    private final KafkaProcessor kafkaProcessor;
    private final DataBaseProcessor dataBaseProcessor;

    public SpecRoute(EmailProcessor emailProcessor, KafkaProcessor kafkaProcessor, DataBaseProcessor dataBaseProcessor) {
        this.emailProcessor = emailProcessor;
        this.kafkaProcessor = kafkaProcessor;
        this.dataBaseProcessor = dataBaseProcessor;
    }


    @Override
    public void configure() throws Exception {
        from("timer:test?period=5000")
                .setBody(constant("Order #1"))
                .multicast()
                .to("direct:email")
                .to("direct:kafka")
                .to("direct:database");

        from("direct:email")
                .process(emailProcessor);

        from("direct:kafka")
                .process(kafkaProcessor);

        from("direct:database")
                .process(dataBaseProcessor);

    }
}
