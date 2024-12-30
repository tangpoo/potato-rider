package com.potatorider.util

import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import reactor.core.publisher.Mono

fun statusIsExpected(delivery: Delivery, expected: DeliveryStatus): Mono<Delivery> =
    when (delivery.deliveryStatus) {
        expected -> Mono.just(delivery)
        else -> Mono.error(
            IllegalStateException(
                String.format("주문 상태가 %s 가 아닙니다.", expected)
            )
        )
    }