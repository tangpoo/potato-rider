package com.potatorider.publisher;

import com.potatorider.domain.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryMessagePublisherImpl implements DeliveryPublisher {

    private final AmqpTemplate messageQueue;

    private final String shopExchange = "messageQueue.exchange.shop";

    @Override
    public Mono<Delivery> sendAddDeliveryEvent(final Delivery delivery) {
        return Mono.just(delivery)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::publishAddDeliveryEvent);
    }

    private Mono<Delivery> publishAddDeliveryEvent(Delivery delivery) {
        return Mono.fromCallable(
            () -> {
                this.messageQueue.convertAndSend(shopExchange, delivery);
                return delivery;
            }
        );
    }
}
