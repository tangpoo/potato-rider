package com.potatorider.repository

import com.potatorider.domain.RiderLocation
import lombok.RequiredArgsConstructor
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
open class RiderLocationRepositoryImpl(private val locationOperations: ReactiveRedisTemplate<String, RiderLocation>) : RiderLocationRepository {

    override fun setIfPresent(riderLocation: RiderLocation): Mono<Boolean> {
        return locationOperations.opsForValue().setIfPresent(riderLocation.id, riderLocation)
    }

    override fun setIfAbsent(riderLocation: RiderLocation): Mono<Boolean> {
        return locationOperations.opsForValue().setIfAbsent(riderLocation.id, riderLocation)
    }

    override fun getLocation(locationId: String): Mono<RiderLocation> {
        return locationOperations.opsForValue()[locationId]
    }
}
