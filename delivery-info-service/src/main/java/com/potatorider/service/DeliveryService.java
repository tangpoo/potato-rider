package com.potatorider.service;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.COMPLETE;
import static com.potatorider.domain.DeliveryStatus.REQUEST;
import static com.potatorider.domain.DeliveryStatus.RIDER_SET;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import com.potatorider.exception.DeliveryNotFountException;
import com.potatorider.publisher.DeliveryPublisher;
import com.potatorider.repository.DeliveryRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPublisher deliveryPublisher;

    public Mono<Delivery> saveDelivery(final Delivery delivery) {
        return deliveryRepository.save(delivery)
            .flatMap(deliveryPublisher::sendAddDeliveryEvent);
    }

    public Mono<Delivery> acceptDelivery(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .flatMap(DeliveryValidator::statusIsNotNull)
            .flatMap(delivery -> DeliveryValidator.statusIsExpected(delivery, REQUEST))
            .map(Delivery::nextStatus)
            .flatMap(deliveryRepository::save)
            .flatMap(deliveryPublisher::sendSetRiderEvent);
    }

    public Mono<Delivery> setDeliveryRider(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .flatMap(DeliveryValidator::statusIsNotNull)
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
            .flatMap(delivery -> DeliveryValidator.statusIsExpected(delivery, COMPLETE)
            .map(Delivery::nextStatus)
            .map(Delivery::setFinishTime)
            .flatMap(deliveryRepository::save));
    }

    public Mono<Delivery> findDelivery(final String deliveryId) {
        return deliveryRepository
            .findById(deliveryId)
            .switchIfEmpty(Mono.error(DeliveryNotFountException::new));
    }

    public Flux<Delivery> findAllDelivery(final int page, final int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return deliveryRepository.findAllByOrderIdContaining("", pageRequest);
    }

    private static class DeliveryValidator {

        public static Mono<Delivery> statusIsNotNull(Delivery delivery) {
            return Objects.isNull(delivery.getDeliveryStatus())
                ? Mono.error(new IllegalStateException("주문상태가 null 입니다."))
                : Mono.just(delivery);
        }

        public static Mono<Delivery> statusIsExpected(Delivery delivery, DeliveryStatus expected) {
            return delivery.getDeliveryStatus().equals(expected)
                ? Mono.just(delivery)
                : Mono.error(
                    new IllegalStateException(String.format("주문 상태가 %s 가 아닙니다.", expected)));
        }
    }
}
