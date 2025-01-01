package com.potatorider.repository

import com.potatorider.domain.ReceiverType
import com.potatorider.domain.RelayRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface RelayRepository : ReactiveMongoRepository<RelayRequest, String> {
    fun findAllByReceiverTypeContaining(
        pageable: Pageable, receiverType: ReceiverType
    ): Flux<RelayRequest>

    fun findAllByAcceptedAndEnabled(isAccepted: Boolean, enabled: Boolean): Flux<RelayRequest>
}
