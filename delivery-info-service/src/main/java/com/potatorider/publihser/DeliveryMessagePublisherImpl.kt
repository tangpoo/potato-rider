package com.potatorider.publihser

import com.potatorider.domain.Delivery
import lombok.extern.slf4j.Slf4j
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.core.scheduler.Schedulers.*

@Component
@Slf4j
class DeliveryMessagePublisherImpl(
    private val messageQueue: AmqpTemplate
) : DeliveryPublisher {

    companion object {
        private const val SHOP_EXCHANGE = "messageQueue.exchange.shop"
        private const val AGENCY_EXCHANGE = "messageQueue.exchange.agency"
    }

    override fun sendAddDeliveryEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.just(delivery)
            .subscribeOn(boundedElastic())
            .flatMap(::publishAddDeliveryEvent)
    }

    override fun sendSetRiderEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.just(delivery)
            .subscribeOn(boundedElastic())
            .flatMap(::publishSetRiderEvent)
    }

    private fun publishSetRiderEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.fromCallable {
            messageQueue.convertAndSend(AGENCY_EXCHANGE, "setRider", delivery)
            delivery
        }
    }

    private fun publishAddDeliveryEvent(delivery: Delivery): Mono<Delivery> {
        return Mono.fromCallable {
            messageQueue.convertAndSend(SHOP_EXCHANGE, "addDelivery", delivery)
            delivery
        }
    }
}
