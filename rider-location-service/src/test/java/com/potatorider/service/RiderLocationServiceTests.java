package com.potatorider.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.potatorider.domain.RiderLocation;
import com.potatorider.domain.RiderLocationSteps;
import com.potatorider.repository.DeliveryRepository;
import com.potatorider.repository.RiderLocationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RiderLocationServiceTests {

    @InjectMocks
    private RiderLocationService riderLocationService;

    @Mock
    private RiderLocationRepository riderLocationRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Nested
    class Try_put_operation {

        @Test
        void location_is_present_redis() {
            // Arrange
            final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();

            when(riderLocationRepository.setIfPresent(riderLocation)).thenReturn(Mono.just(true));

            // Act
            var result = riderLocationService.tryPutOperation(riderLocation);

            // Assert
            StepVerifier.create(result).expectNext(true).verifyComplete();
            verify(riderLocationRepository, times(1)).setIfPresent(riderLocation);
            verify(deliveryRepository, times(0)).isPickedUp(riderLocation.getDeliveryId());
        }

        @Test
        void delivery_not_found_and_is_not_picked_up() {
            // Arrange
            final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();

            when(riderLocationRepository.setIfPresent(riderLocation)).thenReturn(Mono.just(false));
            when(deliveryRepository.isPickedUp(riderLocation.getDeliveryId())).thenReturn(
                Mono.just(false));

            // Act
            var result = riderLocationService.tryPutOperation(riderLocation);

            // Assert
            StepVerifier.create(result).expectNext(false).verifyComplete();
            verify(riderLocationRepository, times(1)).setIfPresent(riderLocation);
            verify(deliveryRepository, times(1)).isPickedUp(riderLocation.getDeliveryId());
            verify(riderLocationRepository, times(0)).setIfAbsent(riderLocation);
        }

        @Test
        void delivery_not_found_and_is_picked_up() {
            // Arrange
            final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();

            when(riderLocationRepository.setIfPresent(riderLocation)).thenReturn(Mono.just(false));
            when(deliveryRepository.isPickedUp(riderLocation.getDeliveryId())).thenReturn(
                Mono.just(true));
            when(riderLocationRepository.setIfAbsent(riderLocation)).thenReturn(Mono.just(true));

            // Act
            var result = riderLocationService.tryPutOperation(riderLocation);

            // Assert
            StepVerifier.create(result).expectNext(true).verifyComplete();
            verify(riderLocationRepository, times(1)).setIfPresent(riderLocation);
            verify(deliveryRepository, times(1)).isPickedUp(riderLocation.getDeliveryId());
            verify(riderLocationRepository, times(1)).setIfAbsent(riderLocation);
        }
    }
}
