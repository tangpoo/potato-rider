package com.potatorider.service;

import com.potatorider.domain.Delivery;
import com.potatorider.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Mono<Delivery> saveDelivery(final Delivery delivery) {
        return deliveryRepository.save(delivery);
    }
}
