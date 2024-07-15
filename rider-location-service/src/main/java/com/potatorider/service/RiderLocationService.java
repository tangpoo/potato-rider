package com.potatorider.service;

import com.potatorider.domain.RiderLocation;
import com.potatorider.repository.DeliveryRepository;
import com.potatorider.repository.RiderLocationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiderLocationService {

    private final RiderLocationRepository riderLocationRepository;
    private final DeliveryRepository deliveryRepository;

    public Mono<Boolean> tryPutOperation(final RiderLocation riderLocation) {
        return riderLocationRepository
                .setIfPresent(riderLocation)
                .flatMap(isSaved -> orElseSetNew(isSaved, riderLocation));
    }

    private Mono<Boolean> orElseSetNew(final Boolean isSaved, final RiderLocation riderLocation) {
        return isSaved ? Mono.just(true) : doSetNew(riderLocation);
    }

    private Mono<Boolean> doSetNew(final RiderLocation riderLocation) {
        return deliveryRepository
                .isPickedUp(riderLocation.getDeliveryId())
                .flatMap(isPickedUp -> setIfPickedUp(isPickedUp, riderLocation));
    }

    private Mono<Boolean> setIfPickedUp(
            final Boolean isPickedUp, final RiderLocation riderLocation) {
        return isPickedUp ? riderLocationRepository.setIfAbsent(riderLocation) : Mono.just(false);
    }

    public Mono<RiderLocation> getLocation(final String locationId) {
        return riderLocationRepository.getLocation(locationId);
    }
}
