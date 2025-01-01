package com.potatorider.subsciber

import com.potatorider.domain.Delivery
import com.potatorider.domain.ReceiverType
import com.potatorider.domain.RelayRequest
import com.potatorider.service.RelayService
import com.potatorider.subscriber.DeliveryMessageSubscriber
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class DeliveryMessageSubscriberTests {

    private val relayService: RelayService = mock()
    private val deliveryMessageSubscriber = DeliveryMessageSubscriber(relayService)

    @Test
    fun add_delivery_message() {
        // Arrange
        val delivery = createInvalidDelivery()
        val relayRequest = createRelayRequest()

        Mockito.`when`(
            relayService.saveDelivery(
                any(),
                eq(ReceiverType.SHOP)
            )
        )
            .thenReturn(Mono.just(relayRequest))

        // Act
        val result = deliveryMessageSubscriber.processAddDeliveryMessage(delivery)

        // Assert
        StepVerifier.create(result).expectNext().verifyComplete()
        Mockito.verify(relayService, Mockito.times(1)).saveDelivery(
            any(),
            any()
        )
    }

    @Test
    fun set_rider_message() {
        // Arrange
        val delivery = createInvalidDelivery()

        Mockito.`when`(
            relayService.saveDelivery(
                any(),
                any()
            )
        )
            .thenReturn(Mono.empty())

        // Act
        val result = deliveryMessageSubscriber.processSetRiderMessage(delivery)

        // Assert
        StepVerifier.create(result).expectNext().verifyComplete()
        Mockito.verify(relayService, Mockito.times(1)).saveDelivery(
            any(),
            any()
        )
    }

    private fun createInvalidDelivery() = Delivery(
        orderId = "",
        shopId = "1L",
        customerId = "",
        address = "",
        phoneNumber = "",
        orderTime = LocalDateTime.now()
    )

    private fun createRelayRequest() = RelayRequest(
        receiverType = ReceiverType.AGENCY,
        receiverId = "",
        delivery = createInvalidDelivery()
    )
}
