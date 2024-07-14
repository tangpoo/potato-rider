package com.potatorider.controller;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.RelayRequest;
import com.potatorider.service.RelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
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

    @GetMapping("/shop")
    public Flux<RelayRequest> findAllRequest(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return relayService.findAllByShop(page, size);
    }

    @GetMapping("/agency")
    public Flux<RelayRequest> findAllAgency(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return relayService.findAllByAgency(page, size);
    }
}
