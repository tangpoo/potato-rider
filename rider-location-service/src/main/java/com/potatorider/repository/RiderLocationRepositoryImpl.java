package com.potatorider.repository;

import com.potatorider.domain.RiderLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RiderLocationRepositoryImpl implements RiderLocationRepository{

    private final ReactiveRedisTemplate<String, RiderLocation> locationOperations;

    @Override
    public Mono<Boolean> setIfPresent(final RiderLocation riderLocation) {
        return locationOperations.opsForValue().setIfPresent(riderLocation.getId(), riderLocation);
    }

    @Override
    public Mono<Boolean> setIfAbsent(final RiderLocation riderLocation) {
        return locationOperations.opsForValue().setIfAbsent(riderLocation.getId(), riderLocation);
    }

    @Override
    public Mono<RiderLocation> getLocation(final String deliveryId) {
        return locationOperations.opsForValue().get(deliveryId);
    }
}
