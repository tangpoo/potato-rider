package com.potatorider.controller;

import com.potatorider.config.swagger.DeliveryControllerSwaggerDoc;
import com.potatorider.domain.Delivery;
import com.potatorider.service.DeliveryService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@Slf4j
public class DeliveryController implements DeliveryControllerSwaggerDoc {

    private final DeliveryService deliveryService;

    @PostMapping
    public Mono<Delivery> saveDelivery(@RequestBody @Valid Delivery delivery) {
        return deliveryService.saveDelivery(delivery);
    }

    @PutMapping("/{deliveryId}/accept")
    public Mono<Delivery> acceptDelivery(@PathVariable String deliveryId) {
        return deliveryService.acceptDelivery(deliveryId);
    }

    @PutMapping("/{deliveryId}/rider")
    public Mono<Delivery> setDeliveryRider(@PathVariable String deliveryId) {
        return deliveryService.setDeliveryRider(deliveryId);
    }

    @PutMapping("/{deliveryId}/pickup")
    public Mono<Delivery> pickUpDelivery(@PathVariable String deliveryId) {
        return deliveryService.pickUpDelivery(deliveryId);
    }

    @PutMapping("/{deliveryId}/complete")
    public Mono<Delivery> completeDelivery(@PathVariable String deliveryId) {
        return deliveryService.completeDelivery(deliveryId);
    }

    @GetMapping("/{deliveryId}")
    public Mono<Delivery> findDelivery(@PathVariable String deliveryId) {
        return deliveryService.findDelivery(deliveryId);
    }

    @GetMapping
    public Flux<Delivery> findAllDelivery(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {
        return deliveryService.findAllDelivery(page, size);
    }

    @GetMapping("/{deliveryId}/is-picked-up")
    public Mono<Boolean> DeliveryIsPickedUp(@PathVariable String deliveryId) {
        return deliveryService.isPickedUp(deliveryId);
    }
}
