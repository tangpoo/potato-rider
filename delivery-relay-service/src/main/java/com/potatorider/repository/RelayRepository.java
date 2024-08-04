package com.potatorider.repository;

import com.potatorider.domain.ReceiverType;
import com.potatorider.domain.RelayRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface RelayRepository extends ReactiveMongoRepository<RelayRequest, String> {

    Flux<RelayRequest> findAllByReceiverTypeContaining(
            Pageable pageable, ReceiverType receiverType);

    Flux<RelayRequest> findAllByIsAcceptedAndIsEnabled(boolean isAccepted, boolean enabled);
}
