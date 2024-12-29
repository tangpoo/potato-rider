package com.potatorider.util;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import reactor.core.publisher.Mono;

public class DeliveryValidator {
        public static Mono<Delivery> statusIsExpected(Delivery delivery, DeliveryStatus expected) {
            return delivery.getDeliveryStatus().equals(expected)
                    ? Mono.just(delivery)
                    : Mono.error(
                            new IllegalStateException(
                                    String.format("주문 상태가 %s 가 아닙니다.", expected)));
        }
}
