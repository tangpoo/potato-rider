package com.potatorider.controller;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.RelayRequest;
import com.potatorider.service.RelayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/relay")
public class RelayController {

    private final RelayService relayService;

    @PostMapping
    public Mono<RelayRequest> saveRequest(@RequestBody Delivery delivery) {
        return relayService.saveDelivery(delivery);
    }
}
