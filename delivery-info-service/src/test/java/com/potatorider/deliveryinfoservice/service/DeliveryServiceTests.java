package com.potatorider.deliveryinfoservice.service;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.PICKED_UP;
import static com.potatorider.domain.DeliveryStatus.REQUEST;
import static com.potatorider.domain.DeliveryStatus.RIDER_SET;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.potatorider.deliveryinfoservice.domain.DeliverySteps;
import com.potatorider.domain.Delivery;
import com.potatorider.exception.DeliveryNotFoundException;
import com.potatorider.publisher.DeliveryPublisher;
import com.potatorider.repository.DeliveryRepository;
import com.potatorider.service.DeliveryService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTests {

  @InjectMocks DeliveryService deliveryService;

  @Mock DeliveryRepository deliveryRepository;

  @Mock DeliveryPublisher deliveryPublisher;

  @Nested
  class Save_delivery {

    @Test
    void success() {
      // Arrange
      final Delivery delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(null);

      when(deliveryRepository.save(isA(Delivery.class))).thenReturn(Mono.just(delivery));
      when(deliveryPublisher.sendAddDeliveryEvent(isA(Delivery.class)))
          .thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.saveDelivery(delivery);

      // Assert
      StepVerifier.create(result).expectNext(delivery).verifyComplete();
      verify(deliveryRepository, times(1)).save(delivery);
      verify(deliveryPublisher, times(1)).sendAddDeliveryEvent(delivery);
    }
  }

  @Nested
  class Accept_delivery {

    @Test
    void success() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));
      when(deliveryRepository.save(isA(Delivery.class))).thenReturn(Mono.just(delivery));
      when(deliveryPublisher.sendSetRiderEvent(any())).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.acceptDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectNext(delivery).verifyComplete();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(1)).save(delivery);
      verify(deliveryPublisher, times(1)).sendSetRiderEvent(delivery);
    }

    @Test
    void fail_not_found() {
      // Arrange
      String deliveryId = "delivery-1234";

      when(deliveryRepository.findById(anyString()))
          .thenReturn(Mono.error(DeliveryNotFoundException::new));

      // Act
      var result = deliveryService.acceptDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectError(DeliveryNotFoundException.class).verify();
      verify(deliveryRepository, times(0)).save(any());
      verify(deliveryPublisher, times(0)).sendSetRiderEvent(any());
    }

    @Test
    void fail_delivery_status_is_not_request() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(ACCEPT);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.acceptDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectError(IllegalStateException.class).verify();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(0)).save(any());
      verify(deliveryPublisher, times(0)).sendSetRiderEvent(any());
    }
  }

  @Nested
  class Set_delivery_rider {

    @Test
    void success() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(ACCEPT);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));
      when(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.setDeliveryRider(deliveryId);

      // Assert
      StepVerifier.create(result).expectNext(delivery).verifyComplete();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void fail_delivery_status_is_not_accept() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.setDeliveryRider(deliveryId);

      // Assert
      StepVerifier.create(result).expectError(IllegalStateException.class).verify();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(0)).save(any());
    }
  }

  @Nested
  class Pick_up_delivery {

    @Test
    void success() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(RIDER_SET);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));
      when(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.pickUpDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectNext(delivery).verifyComplete();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void fail_delivery_status_is_not_set_rider() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.pickUpDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectError(IllegalStateException.class).verify();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(0)).save(any());
    }
  }

  @Nested
  class Complete_delivery {

    @Test
    void success() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(PICKED_UP);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));
      when(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.completeDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectNext(delivery).verifyComplete();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void fail_delivery_status_is_not_picked_up() {
      // Arrange
      String deliveryId = "delivery-1234";
      var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

      when(deliveryRepository.findById(anyString())).thenReturn(Mono.just(delivery));

      // Act
      var result = deliveryService.completeDelivery(deliveryId);

      // Assert
      StepVerifier.create(result).expectError(IllegalStateException.class).verify();
      verify(deliveryRepository, times(1)).findById(deliveryId);
      verify(deliveryRepository, times(0)).save(any());
    }
  }

  @Nested
  class Retry_back_of_spec {

    /*todo 재시도가 수행되지 않음을 발견

    @Test
    void retries_on_timeout_exception() {
        // Arrange
        final Delivery delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

        when(deliveryRepository.save(delivery)).thenReturn(Mono.just(delivery));
        when(deliveryPublisher.sendAddDeliveryEvent(any(Delivery.class)))
            .thenReturn(Mono.error(new TimeoutException()));
        // Act
        var result = deliveryService.saveDelivery(delivery);

        // Assert
        StepVerifier.create(result).expectError(RetryExhaustedException.class).verify();
        verify(deliveryPublisher, times(1)).sendAddDeliveryEvent(any(Delivery.class));
    }
    */
  }
}
