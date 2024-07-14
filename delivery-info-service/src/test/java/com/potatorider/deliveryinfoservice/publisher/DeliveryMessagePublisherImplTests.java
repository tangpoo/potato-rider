package com.potatorider.deliveryinfoservice.publisher;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.REQUEST;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.potatorider.deliveryinfoservice.domain.DeliverySteps;
import com.potatorider.domain.Delivery;
import com.potatorider.publihser.DeliveryMessagePublisherImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DeliveryMessagePublisherImplTests {

    private final String shopExchange = "messageQueue.exchange.shop";
    private final String agencyExchange = "messageQueue.exchange.agency";
    DeliveryMessagePublisherImpl deliveryPublisher;
    AmqpTemplate amqpTemplate;

    @BeforeEach
    void setUp() {
        amqpTemplate = mock(AmqpTemplate.class);
        deliveryPublisher = new DeliveryMessagePublisherImpl(amqpTemplate);
    }

    @Test
    void send_add_delivery_event() {
        // Arrange
        var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

        // Act
        var result = deliveryPublisher.sendAddDeliveryEvent(delivery);

        // Assert
        StepVerifier.create(result).expectNext(delivery).verifyComplete();
        verify(amqpTemplate, times(1))
                .convertAndSend(eq(shopExchange), any(String.class), any(Delivery.class));
    }

    @Test
    void send_set_rider_event() {
        // Arrange
        var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(ACCEPT);

        // Act
        var result = deliveryPublisher.sendSetRiderEvent(delivery);

        // Assert
        StepVerifier.create(result).expectNext(delivery).verifyComplete();
        verify(amqpTemplate, times(1))
                .convertAndSend(eq(agencyExchange), any(String.class), any(Delivery.class));
    }
}
