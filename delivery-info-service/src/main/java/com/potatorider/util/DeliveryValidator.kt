package com.potatorider.util

import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import reactor.core.publisher.Mono

fun Delivery.statusExpectIs(expectStatus: DeliveryStatus): Mono<Delivery> =
    when (deliveryStatus) {
        expectStatus -> Mono.just(this)
        else -> Mono.error(
            IllegalStateException(
                String.format("주문 상태가 %s 가 아닙니다.", expectStatus)
            )
        )
    }