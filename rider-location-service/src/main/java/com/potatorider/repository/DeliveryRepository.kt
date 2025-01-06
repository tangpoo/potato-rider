package com.potatorider.repository

import reactor.core.publisher.Mono

interface DeliveryRepository {
    fun isPickedUp(deliveryId: String): Mono<Boolean>
}
