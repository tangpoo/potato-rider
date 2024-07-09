package com.potatorider.deliveryinfoservice.publisher;

import static com.potatorider.domain.DeliveryStatus.*;
import static org.mockito.Mockito.*;

import com.potatorider.deliveryinfoservice.domain.DeliverySteps;
import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import com.potatorider.publisher.DeliveryMessagePublisherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DeliveryMessagePublisherImplTests {

    DeliveryMessagePublisherImpl deliveryPublisher;

    AmqpTemplate amqpTemplate;

    private final String shopExchange = "messageQueue.exchange.shop";
    private final String agencyExchange = "messageQueue.exchange.agency";

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
            .convertAndSend(eq(shopExchange), any(Delivery.class));
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
            .convertAndSend(eq(agencyExchange), any(Delivery.class));
    }
}
