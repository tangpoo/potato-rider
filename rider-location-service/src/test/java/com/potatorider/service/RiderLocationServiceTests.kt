package com.potatorider.service

import com.potatorider.domain.createRiderLocation
import com.potatorider.repository.DeliveryRepository
import com.potatorider.repository.RiderLocationRepository
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class RiderLocationServiceTests{

    @InjectMocks
    private lateinit var riderLocationService: RiderLocationService

    @Mock
    private lateinit var riderLocationRepository: RiderLocationRepository

    @Mock
    private lateinit var deliveryRepository: DeliveryRepository

    @Nested
    internal inner class TryPutOperation {
        @Test
        fun location_is_present_redis() {
            // Arrange
            val riderLocation = createRiderLocation()

            Mockito.`when`(riderLocationRepository.setIfPresent(riderLocation))
                .thenReturn(Mono.just(true))

            // Act
            val result = riderLocationService.tryPutOperation(riderLocation)

            // Assert
            StepVerifier.create(result).expectNext(true).verifyComplete()
            Mockito.verify(riderLocationRepository, Mockito.times(1)).setIfPresent(riderLocation)
            Mockito.verify(deliveryRepository, Mockito.times(0)).isPickedUp(
                riderLocation.deliveryId!!
            )
        }

        @Test
        fun delivery_not_found_and_is_not_picked_up() {
            // Arrange
            val riderLocation = createRiderLocation()

            Mockito.`when`(riderLocationRepository.setIfPresent(riderLocation))
                .thenReturn(Mono.just(false))
            Mockito.`when`(deliveryRepository.isPickedUp(riderLocation.deliveryId!!))
                .thenReturn(Mono.just(false))

            // Act
            val result = riderLocationService.tryPutOperation(riderLocation)

            // Assert
            StepVerifier.create(result).expectNext(false).verifyComplete()
            Mockito.verify(riderLocationRepository, Mockito.times(1)).setIfPresent(riderLocation)
            Mockito.verify(deliveryRepository, Mockito.times(1)).isPickedUp(
                riderLocation.deliveryId!!
            )
            Mockito.verify(riderLocationRepository, Mockito.times(0)).setIfAbsent(riderLocation)
        }

        @Test
        fun delivery_not_found_and_is_picked_up() {
            // Arrange
            val riderLocation = createRiderLocation()

            Mockito.`when`(riderLocationRepository.setIfPresent(riderLocation))
                .thenReturn(Mono.just(false))
            Mockito.`when`(deliveryRepository.isPickedUp(riderLocation.deliveryId!!))
                .thenReturn(Mono.just(true))
            Mockito.`when`(riderLocationRepository.setIfAbsent(riderLocation))
                .thenReturn(Mono.just(true))

            // Act
            val result = riderLocationService.tryPutOperation(riderLocation)

            // Assert
            StepVerifier.create(result).expectNext(true).verifyComplete()
            Mockito.verify(riderLocationRepository, Mockito.times(1)).setIfPresent(riderLocation)
            Mockito.verify(deliveryRepository, Mockito.times(1)).isPickedUp(
                riderLocation.deliveryId!!
            )
            Mockito.verify(riderLocationRepository, Mockito.times(1)).setIfAbsent(riderLocation)
        }
    }
}
