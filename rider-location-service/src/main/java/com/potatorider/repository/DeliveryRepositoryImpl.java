package com.potatorider.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class DeliveryRepositoryImpl implements DeliveryRepository{

    @Value("http://localhost:${wiremock.server.port}")
    private String uriDeliveryInfoService;

    private final WebClient webClient;

    public DeliveryRepositoryImpl() {
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Boolean> isPickedUp(final String deliveryId) {
        return webClient
            .get()
            .uri(uriDeliveryInfoService + "/is-picked-up/" + deliveryId)
            .retrieve()
            .bodyToMono(Boolean.class);
    }
}
