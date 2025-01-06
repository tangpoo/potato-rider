package com.potatorider.deliveryinfoservice.publisher

import com.potatorider.deliveryinfoservice.domain.makeValidDeliveryWithDeliveryStatus
import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import com.potatorider.publihser.DeliveryMessagePublisherImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.core.AmqpTemplate
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class DeliveryMessagePublisherImplTests {
    private val shopExchange = "messageQueue.exchange.shop"
    private val agencyExchange = "messageQueue.exchange.agency"
    private lateinit var deliveryPublisher: DeliveryMessagePublisherImpl
    private lateinit var amqpTemplate: AmqpTemplate

    @BeforeEach
    fun setUp() {
        amqpTemplate = mock(AmqpTemplate::class.java)
        val actualDeliveryPublisher = DeliveryMessagePublisherImpl(amqpTemplate)
        deliveryPublisher = spy(actualDeliveryPublisher)
    }

    @Test
    fun send_add_delivery_event() {
        // Arrange
        val delivery = makeValidDeliveryWithDeliveryStatus(DeliveryStatus.REQUEST)

        // Act
        val result = deliveryPublisher.sendAddDeliveryEvent(delivery)

        // Assert
        StepVerifier.create(result).expectNext(delivery).verifyComplete()
        verify(amqpTemplate, times(1))
            .convertAndSend(
                eq(shopExchange), any(
                    String::class.java
                ), any(Delivery::class.java)
            )
    }

    @Test
    fun send_set_rider_event() {
        // Arrange
        val delivery = makeValidDeliveryWithDeliveryStatus(DeliveryStatus.ACCEPT)

        // Act
        val result = deliveryPublisher.sendSetRiderEvent(delivery)

        // Assert
        StepVerifier.create(result).expectNext(delivery).verifyComplete()
        verify(amqpTemplate, times(1))
            .convertAndSend(
                eq(agencyExchange), any(
                    String::class.java
                ), any(Delivery::class.java)
            )
    }
}
