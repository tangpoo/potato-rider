package com.potatorider.deliveryinfoservice.domain;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.COMPLETE;
import static com.potatorider.domain.DeliveryStatus.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import com.potatorider.domain.Delivery;
import org.junit.jupiter.api.Test;

public class DeliveryTests {

    @Test
    void next_status_is_not_last() {
        // Arrange
        Delivery delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);

        // Act
        final Delivery response = delivery.nextStatus();

        // Assert
        assertThat(response.getDeliveryStatus()).isEqualTo(ACCEPT);
    }

    @Test
    void next_status_is_last() {
        // Arrange
        Delivery delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(COMPLETE);

        // Act
        final Delivery response = delivery.nextStatus();

        // Assert
        assertThat(response.getDeliveryStatus()).isEqualTo(COMPLETE);
    }

}
