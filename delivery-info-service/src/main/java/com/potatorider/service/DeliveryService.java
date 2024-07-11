package com.potatorider.service;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.PICKED_UP;
import static com.potatorider.domain.DeliveryStatus.REQUEST;
import static com.potatorider.domain.DeliveryStatus.RIDER_SET;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import com.potatorider.exception.DeliveryNotFoundException;
import com.potatorider.exception.RetryExhaustedException;
import com.potatorider.publisher.DeliveryPublisher;
import com.potatorider.repository.DeliveryRepository;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPublisher deliveryPublisher;
    private final static Long MAX_ATTEMPTS = 3L;
    private final static Duration FIXED_DELAY = Duration.ofMillis(500);

    public Mono<Delivery> saveDelivery(final Delivery delivery) {
        return deliveryRepository.save(delivery.setDeliveryStatusRequest())
            .flatMap(del -> deliveryPublisher.sendAddDeliveryEvent(del)
                .retryWhen(retryBackoffSpec()));
    }

    public Mono<Delivery> acceptDelivery(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .flatMap(delivery -> DeliveryValidator.statusIsExpected(delivery, REQUEST))
            .map(Delivery::nextStatus)
            .flatMap(deliveryRepository::save)
            .flatMap(del -> deliveryPublisher.sendSetRiderEvent(del)
                .retryWhen(retryBackoffSpec()));
    }

    public Mono<Delivery> setDeliveryRider(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .flatMap(delivery -> DeliveryValidator.statusIsExpected(delivery, ACCEPT))
            .map(Delivery::nextStatus)
            .flatMap(deliveryRepository::save);
    }

    public Mono<Delivery> pickUpDelivery(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .flatMap(delivery -> DeliveryValidator.statusIsExpected(delivery, RIDER_SET))
            .map(Delivery::nextStatus)
            .map(Delivery::setPickupTime)
            .flatMap(deliveryRepository::save);
    }

    public Mono<Delivery> completeDelivery(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .flatMap(delivery -> DeliveryValidator.statusIsExpected(delivery, PICKED_UP)
                .map(Delivery::nextStatus)
                .map(Delivery::setFinishTime)
                .flatMap(deliveryRepository::save));
    }

    public Mono<Delivery> findDelivery(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .switchIfEmpty(Mono.error(DeliveryNotFoundException::new));
    }

    public Flux<Delivery> findAllDelivery(final int page, final int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryRepository.findAllBy(pageable);
    }

    private RetryBackoffSpec retryBackoffSpec() {
        return Retry.fixedDelay(MAX_ATTEMPTS, FIXED_DELAY)
            .filter(
                (ex) -> ex instanceof TimeoutException)
            .onRetryExhaustedThrow(
                (((retryBackoffSpec, retrySignal) -> new RetryExhaustedException(retrySignal))));
    }

    private static class DeliveryValidator {

        public static Mono<Delivery> statusIsExpected(Delivery delivery, DeliveryStatus expected) {
            return delivery.getDeliveryStatus().equals(expected)
                ? Mono.just(delivery)
                : Mono.error(
                    new IllegalStateException(String.format("주문 상태가 %s 가 아닙니다.", expected)));
        }
    }
}
