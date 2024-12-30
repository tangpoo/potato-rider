package com.potatorider.repository;

import com.potatorider.domain.Delivery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface DeliveryRepository extends ReactiveMongoRepository<Delivery, String> {

    Flux<Delivery> findAllBy(Pageable pageable);
}
