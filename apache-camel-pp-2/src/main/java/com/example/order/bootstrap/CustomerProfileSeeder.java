package com.example.order.bootstrap;

import com.example.order.domain.CustomerSegment;
import com.example.order.domain.CustomerStatus;
import com.example.order.persistence.CustomerProfileEntity;
import com.example.order.persistence.CustomerProfileRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;

@Configuration
public class CustomerProfileSeeder {

    @Bean
    ApplicationRunner seedCustomerProfiles(CustomerProfileRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(new CustomerProfileEntity(
                    "cust-1001",
                    CustomerStatus.ACTIVE,
                    CustomerSegment.NEW,
                    0,
                    new BigDecimal("0.00"),
                    Instant.now()
            ));
            repository.save(new CustomerProfileEntity(
                    "cust-1002",
                    CustomerStatus.ACTIVE,
                    CustomerSegment.GOLD,
                    12,
                    new BigDecimal("14820.00"),
                    Instant.now()
            ));
            repository.save(new CustomerProfileEntity(
                    "cust-1003",
                    CustomerStatus.REVIEW,
                    CustomerSegment.STANDARD,
                    3,
                    new BigDecimal("980.00"),
                    Instant.now()
            ));
        };
    }
}
