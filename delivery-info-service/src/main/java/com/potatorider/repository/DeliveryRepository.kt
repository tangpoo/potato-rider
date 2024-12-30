package com.potatorider.repository

import com.potatorider.domain.Delivery
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface DeliveryRepository : ReactiveMongoRepository<Delivery, String> {
    fun findAllBy(pageable: Pageable): Flux<Delivery>
}
