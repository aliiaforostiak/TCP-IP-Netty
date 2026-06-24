package com.example.order.bootstrap;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Instant;

@Configuration
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
public class DemoOrderPublisher {

    @Bean
    ApplicationRunner publishDemoOrder(ProducerTemplate producerTemplate, @Value("${app.kafka.orders-in-topic}") String ordersInTopic) {
        return args -> {
            String payload = """
                    {
                      "orderId": "demo-order-%s",
                      "customerId": "cust-1001",
                      "items": [
                        {
                          "sku": "sku-demo-1",
                          "name": "Demo widget",
                          "quantity": 2,
                          "unitPrice": 10.00
                        }
                      ],
                      "totalAmount": 20.00,
                      "currency": "USD",
                      "createdAt": "%s",
                      "promoCode": "SUMMER7"
                    }
                    """.formatted(Instant.now().toEpochMilli(), Instant.now().toString());

            producerTemplate.sendBody("kafka:" + ordersInTopic, payload);
        };
    }
}
