package com.potatorider.service;

import static com.potatorider.domain.DeliveryStatus.REQUEST;

import com.potatorider.domain.Delivery;
import com.potatorider.publisher.DeliveryPublisher;
import com.potatorider.repository.DeliveryRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPublisher deliveryPublisher;

    public Mono<Delivery> saveDelivery(final Delivery delivery) {
        return deliveryRepository.save(delivery).flatMap(deliveryPublisher::sendAddDeliveryEvent);
    }

    public Mono<Delivery> acceptDelivery(final Delivery delivery) {
        return Mono.just(delivery)
            .flatMap(DeliveryValidator::statusIsNotNull)
            .flatMap(DeliveryValidator::statusIsRequest)
            .flatMap(del -> deliveryRepository.save(delivery))
            .flatMap(deliveryPublisher::sendSetRiderEvent);
    }

    private static class DeliveryValidator {

        public static Mono<Delivery> statusIsNotNull(Delivery delivery) {
            return Objects.isNull(delivery.getDeliveryStatus())
                ? Mono.error(new IllegalStateException("주문상태가 null 입니다."))
                : Mono.just(delivery);
        }

        public static Mono<Delivery> statusIsRequest(Delivery delivery) {
            return delivery.getDeliveryStatus().equals(REQUEST)
                ? Mono.just(delivery)
                : Mono.error(new IllegalStateException("주문 상태가 REQUEST 가 아닙니다."));
        }
    }
}
