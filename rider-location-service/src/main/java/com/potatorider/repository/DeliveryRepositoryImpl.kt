package com.potatorider.repository

import lombok.Setter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Repository
open class DeliveryRepositoryImpl(
    private val webClientBuilder: WebClient.Builder
) : DeliveryRepository {

    @Value("\${services.deliveryInfoService.path}")
    lateinit var uriDeliveryInfoService: String

    private val webClient: WebClient = webClientBuilder.build()

    override fun isPickedUp(deliveryId: String): Mono<Boolean> {
        return webClient
            .get()
            .uri("$uriDeliveryInfoService/api/v1/delivery/$deliveryId/is-picked-up")
            .retrieve()
            .bodyToMono(Boolean::class.java)
    }
}
