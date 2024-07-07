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
    private final String agencyExchange = "messageQueue.exchange.agency";

    @Override
    public Mono<Delivery> sendAddDeliveryEvent(final Delivery delivery) {
        return Mono.just(delivery)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::publishAddDeliveryEvent);
    }

    @Override
    public Mono<Delivery> sendSetRiderEvent(final Delivery delivery) {
        return Mono.just(delivery)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::publishSetRiderEvent);
    }

    private Mono<Delivery> publishSetRiderEvent(Delivery delivery) {
        return Mono.fromCallable(
            () -> {
                this.messageQueue.convertAndSend(agencyExchange);
                return delivery;
            }
        );
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
