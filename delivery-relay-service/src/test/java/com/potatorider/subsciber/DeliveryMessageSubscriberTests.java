package com.potatorider.subsciber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.potatorider.domain.Delivery;
import com.potatorider.service.RelayService;
import com.potatorider.subscriber.DeliveryMessageSubscriber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DeliveryMessageSubscriberTests {

    @InjectMocks private DeliveryMessageSubscriber deliveryMessageSubscriber;

    @Mock private RelayService relayService;

    @Test
    public void add_delivery_message() {
        // Arrange
        Delivery delivery = new Delivery();

        when(relayService.saveDelivery(any(Delivery.class))).thenReturn(Mono.empty());

        // Act
        var result = deliveryMessageSubscriber.processAddDeliveryMessage(delivery);

        // Assert
        StepVerifier.create(result).expectNext().verifyComplete();
        verify(relayService, times(1)).saveDelivery(any(Delivery.class));
    }

    @Test
    public void set_rider_message() {
        // Arrange
        Delivery delivery = new Delivery();

        when(relayService.saveDelivery(any(Delivery.class))).thenReturn(Mono.empty());

        // Act
        var result = deliveryMessageSubscriber.processSetRiderMessage(delivery);

        // Assert
        StepVerifier.create(result).expectNext().verifyComplete();
        verify(relayService, times(1)).saveDelivery(any(Delivery.class));
    }
}
