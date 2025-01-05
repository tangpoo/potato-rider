package com.potatorider.service

import com.potatorider.domain.RiderLocation
import com.potatorider.repository.DeliveryRepository
import com.potatorider.repository.RiderLocationRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RiderLocationService(
    private val riderLocationRepository: RiderLocationRepository,
    private val deliveryRepository: DeliveryRepository
) {

    fun tryPutOperation(riderLocation: RiderLocation): Mono<Boolean> {
        return riderLocationRepository
            .setIfPresent(riderLocation)
            .flatMap { isSaved: Boolean -> orElseSetNew(isSaved, riderLocation) }
    }

    private fun orElseSetNew(isSaved: Boolean, riderLocation: RiderLocation): Mono<Boolean> {
        return if (isSaved) Mono.just(true) else doSetNew(riderLocation)
    }

    private fun doSetNew(riderLocation: RiderLocation): Mono<Boolean> {
        return deliveryRepository
            .isPickedUp(riderLocation.deliveryId!!)
            .flatMap { isPickedUp: Boolean -> setIfPickedUp(isPickedUp, riderLocation) }
    }

    private fun setIfPickedUp(
        isPickedUp: Boolean, riderLocation: RiderLocation
    ): Mono<Boolean> {
        return if (isPickedUp) riderLocationRepository.setIfAbsent(riderLocation) else Mono.just(
            false
        )
    }

    fun getLocation(locationId: String): Mono<RiderLocation> {
        return riderLocationRepository.getLocation(locationId)
    }
}
