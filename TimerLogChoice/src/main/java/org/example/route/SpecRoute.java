package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import org.example.processor.AuditProcessor;
import org.example.service.NumberService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecRoute extends RouteBuilder {

    private final AuditProcessor auditProcessor;
    private final NumberService numberService;

    public SpecRoute(AuditProcessor auditProcessor, NumberService numberService) {
        this.auditProcessor = auditProcessor;
        this.numberService = numberService;
    }

    @Override
    public void configure() throws Exception {
        from("timer:test?period=3000")
                .setBody(constant(List.of(10, 20, 30, 60, 70)))
                .split(body())
                .choice()
                .when(simple("${body} > 30"))
                .filter(simple("${body} < 70"))
                .process(auditProcessor)
                .setHeader("source", constant("timer"))
                .process(exchange -> System.out.println("Processed header value: " + exchange.getMessage().getHeader("processed")))
                .end()
                .endChoice()
                .otherwise()
                .process(exchange -> System.out.println("Message's body: " + exchange.getMessage().getBody(Integer.class) + " is less than 30"))
                .to("log:info");
    }
}
