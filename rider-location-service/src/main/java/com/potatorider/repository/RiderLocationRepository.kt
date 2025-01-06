package com.potatorider.repository

import com.potatorider.domain.RiderLocation
import reactor.core.publisher.Mono

interface RiderLocationRepository {
    fun setIfPresent(riderLocation: RiderLocation): Mono<Boolean>

    fun setIfAbsent(riderLocation: RiderLocation): Mono<Boolean>

    fun getLocation(locationId: String): Mono<RiderLocation>
}
