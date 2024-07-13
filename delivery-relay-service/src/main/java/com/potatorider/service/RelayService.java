package com.potatorider.service;

import static com.potatorider.domain.ReceiverType.SHOP;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.RelayRequest;
import com.potatorider.repository.RelayRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RelayService {

    private final RelayRepository relayRepository;

    public Mono<RelayRequest> saveDelivery(final Delivery delivery) {
        RelayRequest relayRequest = new RelayRequest(SHOP, delivery.getShopId(), delivery);
        return relayRepository.save(relayRequest);
    }
}
