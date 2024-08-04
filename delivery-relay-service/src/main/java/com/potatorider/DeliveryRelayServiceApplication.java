package com.potatorider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeliveryRelayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryRelayServiceApplication.class, args);
    }
}
