package com.potatorider.publihser

import com.potatorider.domain.Delivery
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
@Slf4j
class DeliveryMessagePublisherImpl(
    private val messageQueue: AmqpTemplate
) : DeliveryPublisher {

    private val shopExchange = "messageQueue.exchange.shop"
    private val agencyExchange = "messageQueue.exchange.agency"

    override fun sendAddDeliveryEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.just(delivery)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { delivery -> this.publishAddDeliveryEvent(delivery) }
    }

    override fun sendSetRiderEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.just(delivery)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { delivery -> this.publishSetRiderEvent(delivery) }
    }

    private fun publishSetRiderEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.fromCallable {
            messageQueue.convertAndSend(agencyExchange, "setRider", delivery)
            delivery
        }
    }

    private fun publishAddDeliveryEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.fromCallable {
            messageQueue.convertAndSend(shopExchange, "addDelivery", delivery)
            delivery
        }
    }
}
