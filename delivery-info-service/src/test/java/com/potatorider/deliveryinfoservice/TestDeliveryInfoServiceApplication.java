package com.potatorider.deliveryinfoservice;

import com.potatorider.DeliveryInfoServiceApplication;
import org.springframework.boot.SpringApplication;

public class TestDeliveryInfoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(DeliveryInfoServiceApplication::main).run(args);
    }

}
