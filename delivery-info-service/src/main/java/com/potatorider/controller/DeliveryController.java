package com.potatorider.controller;

import com.potatorider.domain.Delivery;
import com.potatorider.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public Mono<Delivery> saveDelivery(@RequestBody Delivery delivery) {
        return deliveryService.saveDelivery(delivery);
    }
}
