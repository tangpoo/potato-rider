package com.potatorider.publihser

import com.potatorider.domain.Delivery
import reactor.core.publisher.Mono

interface DeliveryPublisher {
    fun sendAddDeliveryEvent(delivery: Delivery): Mono<Delivery>

    fun sendSetRiderEvent(delivery: Delivery): Mono<Delivery>
}
