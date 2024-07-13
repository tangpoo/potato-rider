package com.potatorider.subscriber;

import com.potatorider.domain.Delivery;

import reactor.core.publisher.Mono;

public interface DeliveryPublisher {

    Mono<Delivery> sendAddDeliveryEvent(Delivery delivery);

    Mono<Delivery> sendSetRiderEvent(Delivery delivery);
}
