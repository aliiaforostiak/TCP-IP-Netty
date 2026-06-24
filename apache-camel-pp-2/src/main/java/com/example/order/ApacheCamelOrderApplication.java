package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ApacheCamelOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApacheCamelOrderApplication.class, args);
	}

}
