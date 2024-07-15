package com.potatorider.repository;

import com.potatorider.domain.RiderLocation;

import reactor.core.publisher.Mono;

public interface RiderLocationRepository {

    Mono<Boolean> setIfPresent(RiderLocation riderLocation);

    Mono<Boolean> setIfAbsent(RiderLocation riderLocation);

    Mono<RiderLocation> getLocation(String locationId);
}
