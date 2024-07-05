package com.potatorider.deliveryinfoservice;

import org.springframework.boot.SpringApplication;

public class TestDeliveryInfoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(DeliveryInfoServiceApplication::main)
            .with(TestcontainersConfiguration.class).run(args);
    }

}
