package com.potatorider.repository;

import com.potatorider.domain.Delivery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DeliveryRepository extends ReactiveMongoRepository<Delivery, String> {

}
