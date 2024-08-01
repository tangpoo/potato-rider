package com.potatorider.subscriber;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.ReceiverType;
import com.potatorider.domain.RelayRequest;
import com.potatorider.repository.RelayRepository;

import com.potatorider.service.RelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryMessageSubscriber {

    private final RelayService relayService;

    private static final String shopExchange = "messageQueue.exchange.shop";
    private static final String agencyExchange = "messageQueue.exchange.agency";

    @RabbitListener(
            ackMode = "MANUAL",
            id = "addDeliveryMessageListener",
            bindings =
                    @QueueBinding(
                            value = @Queue,
                            exchange = @Exchange(shopExchange),
                            key = "addDelivery"))
    public Mono<Void> processAddDeliveryMessage(Delivery delivery) {
        log.info("Consuming addDelivery     ===>      " + delivery);
        return relayService
                .saveDelivery(delivery)
                .then();
    }

    @RabbitListener(
            ackMode = "MANUAL",
            id = "setRiderMessageListener",
            bindings =
                    @QueueBinding(
                            value = @Queue,
                            exchange = @Exchange(agencyExchange),
                            key = "setRider"))
    public Mono<Void> processSetRiderMessage(Delivery delivery) {
        log.info("Consuming SetRider     ===>      " + delivery);
        return relayService
            .saveDelivery(delivery)
            .then();
    }
}
