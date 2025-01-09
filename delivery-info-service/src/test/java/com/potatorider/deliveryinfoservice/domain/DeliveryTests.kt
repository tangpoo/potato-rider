package com.potatorider.deliveryinfoservice.domain

import com.potatorider.domain.DeliveryStatus.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class DeliveryTests {
    @Test
    fun next_status_is_not_last() {
        // Arrange
        val delivery = makeValidDeliveryWithDeliveryStatus(REQUEST)

        // Act
        val response = delivery.nextStatus()

        // Assert
        assertThat(response.deliveryStatus).isEqualTo(ACCEPT)
    }

    @Test
    fun next_status_is_last() {
        // Arrange
        val delivery = makeValidDeliveryWithDeliveryStatus(COMPLETE)

        // Act
        val response = delivery.nextStatus()

        // Assert
        assertThat(response.deliveryStatus).isEqualTo(COMPLETE)
    }
}
