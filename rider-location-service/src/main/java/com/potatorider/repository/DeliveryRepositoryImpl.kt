package com.potatorider.repository;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Repository
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final WebClient webClient;

    @Value("${services.deliveryInfoService.path}")
    @Setter
    private String uriDeliveryInfoService;

    public DeliveryRepositoryImpl() {
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Boolean> isPickedUp(final String deliveryId) {
        return webClient
                .get()
                .uri(uriDeliveryInfoService + "/api/v1/delivery/" + deliveryId + "/is-picked-up")
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
