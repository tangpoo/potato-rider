package com.potatorider.repository;

import reactor.core.publisher.Mono;

public interface DeliveryRepository {

    Mono<Boolean> isPickedUp(String deliveryId);
}
