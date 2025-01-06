package com.potatorider.deliveryinfoservice.service

import com.potatorider.deliveryinfoservice.domain.makeValidDeliveryWithDeliveryStatus
import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus.*
import com.potatorider.exception.DeliveryNotFoundException
import com.potatorider.publihser.DeliveryPublisher
import com.potatorider.repository.DeliveryRepository
import com.potatorider.service.DeliveryService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class DeliveryServiceTests {
    @InjectMocks
    private lateinit var deliveryService: DeliveryService

    @Mock
    private lateinit var deliveryRepository: DeliveryRepository

    @Mock
    private lateinit var deliveryPublisher: DeliveryPublisher

    @Nested
    internal inner class SaveDelivery {
        @Test
        fun success() {
            // Arrange
            val delivery = makeValidDeliveryWithDeliveryStatus(null)

            Mockito.`when`(
                deliveryRepository.save(
                    argThat { this is Delivery }
                )
            ).thenReturn(Mono.just(delivery))
            Mockito.`when`(
                deliveryPublisher.sendAddDeliveryEvent(
                    argThat { true }
                )
            )
                .thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.saveDelivery(delivery)

            // Assert
            StepVerifier.create(result).expectNext(delivery).verifyComplete()
            Mockito.verify(deliveryRepository, Mockito.times(1)).save(delivery)
            Mockito.verify(deliveryPublisher, Mockito.times(1)).sendAddDeliveryEvent(delivery)
        }
    }

    @Nested
    internal inner class AcceptDelivery {
        @Test
        fun success() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(REQUEST)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))
            Mockito.`when`(
                deliveryRepository.save(
                    argThat { this is Delivery }
                )
            ).thenReturn(Mono.just(delivery))
            Mockito.`when`(deliveryPublisher.sendSetRiderEvent(any()))
                .thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.acceptDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectNext(delivery).verifyComplete()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(1)).save(delivery)
            Mockito.verify(deliveryPublisher, Mockito.times(1)).sendSetRiderEvent(delivery)
        }

        @Test
        fun fail_not_found() {
            // Arrange
            val deliveryId = "delivery-1234"

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.empty())

            // Act
            val result = deliveryService.acceptDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectError(
                DeliveryNotFoundException::class.java
            ).verify()
            Mockito.verify(deliveryRepository, Mockito.times(0)).save(any())
            Mockito.verify(deliveryPublisher, Mockito.times(0))
                .sendSetRiderEvent(any())
        }

        @Test
        fun fail_delivery_status_is_not_request() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(ACCEPT)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.acceptDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectError(IllegalStateException::class.java).verify()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(0)).save(any())
            Mockito.verify(deliveryPublisher, Mockito.times(0))
                .sendSetRiderEvent(any())
        }
    }

    @Nested
    internal inner class SetDeliveryRider {
        @Test
        fun success() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(ACCEPT)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))
            Mockito.`when`(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.setDeliveryRider(deliveryId)

            // Assert
            StepVerifier.create(result).expectNext(delivery).verifyComplete()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(1)).save(delivery)
        }

        @Test
        fun fail_delivery_status_is_not_accept() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(REQUEST)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.setDeliveryRider(deliveryId)

            // Assert
            StepVerifier.create(result).expectError(IllegalStateException::class.java).verify()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(0)).save(any())
        }
    }

    @Nested
    internal inner class PickUpDelivery {
        @Test
        fun success() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(RIDER_SET)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))
            Mockito.`when`(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.pickUpDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectNext(delivery).verifyComplete()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(1)).save(delivery)
        }

        @Test
        fun fail_delivery_status_is_not_set_rider() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(REQUEST)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.pickUpDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectError(IllegalStateException::class.java).verify()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(0)).save(any())
        }
    }

    @Nested
    internal inner class CompleteDelivery {
        @Test
        fun success() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(PICKED_UP)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))
            Mockito.`when`(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.completeDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectNext(delivery).verifyComplete()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(1)).save(delivery)
        }

        @Test
        fun fail_delivery_status_is_not_picked_up() {
            // Arrange
            val deliveryId = "delivery-1234"
            val delivery = makeValidDeliveryWithDeliveryStatus(REQUEST)

            Mockito.`when`(deliveryRepository.findById(anyString()))
                .thenReturn(Mono.just(delivery))

            // Act
            val result = deliveryService.completeDelivery(deliveryId)

            // Assert
            StepVerifier.create(result).expectError(IllegalStateException::class.java).verify()
            Mockito.verify(deliveryRepository, Mockito.times(1)).findById(deliveryId)
            Mockito.verify(deliveryRepository, Mockito.times(0)).save(any())
        }
    }
}
