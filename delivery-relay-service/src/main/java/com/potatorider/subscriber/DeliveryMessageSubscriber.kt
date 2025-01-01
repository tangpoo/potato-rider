package com.potatorider.subscriber

import com.potatorider.domain.Delivery
import com.potatorider.domain.ReceiverType
import com.potatorider.service.RelayService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeliveryMessageSubscriber(private val relayService: RelayService) {

    @RabbitListener(
        ackMode = "MANUAL", id = "addDeliveryMessageListener",
        bindings = [QueueBinding(
            value = Queue(),
            exchange = Exchange(shopExchange),
            key = ["addDelivery"]
        )]
    )
    fun processAddDeliveryMessage(delivery: Delivery): Mono<Void> {
        log.info("Consuming addDelivery     ===>      $delivery")
        return relayService.saveDelivery(delivery, ReceiverType.SHOP).then()
    }

    @RabbitListener(
        ackMode = "MANUAL",
        id = "setRiderMessageListener",
        bindings = [QueueBinding(
            value = Queue(),
            exchange = Exchange(agencyExchange),
            key = ["setRider"]
        )]
    )
    fun processSetRiderMessage(delivery: Delivery): Mono<Void> {
        log.info("Consuming SetRider     ===>      $delivery")
        return relayService.saveDelivery(delivery, ReceiverType.AGENCY).then()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DeliveryMessageSubscriber::class.java)
        private const val shopExchange = "messageQueue.exchange.shop"
        private const val agencyExchange = "messageQueue.exchange.agency"
    }
}
