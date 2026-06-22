package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FirstRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:test?period=3000")
                .setBody(constant("Hello Camel"))
                .setHeader("source", constant("timer"))
                .process(exchange -> {
                    String body = exchange.getMessage().getBody(String.class);
                    String source = exchange.getMessage().getHeader("source", String.class);
                    System.out.println("OLD BODY: " + body);
                    System.out.println("HEADER: " + source);

                    String updatedBody = body.toUpperCase();
                    exchange.getMessage().setBody(updatedBody);
                    System.out.println("UPDATED BODY: " + updatedBody);
                })
                .to("log:info");
    }
}
