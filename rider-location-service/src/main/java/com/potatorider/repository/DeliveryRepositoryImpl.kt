package com.potatorider.repository

import lombok.Setter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Repository
class DeliveryRepositoryImpl : DeliveryRepository {
    private val webClient: WebClient = WebClient.builder().build()

    @Value("\${services.deliveryInfoService.path}")
    @Setter
    private val uriDeliveryInfoService: String? = null

    override fun isPickedUp(deliveryId: String): Mono<Boolean> {
        return webClient
            .get()
            .uri("$uriDeliveryInfoService/api/v1/delivery/$deliveryId/is-picked-up")
            .retrieve()
            .bodyToMono(Boolean::class.java)
    }
}
