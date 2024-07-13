package com.potatorider.repository;

import com.potatorider.domain.RelayRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface RelayRepository extends ReactiveMongoRepository<RelayRequest, String> {

}
