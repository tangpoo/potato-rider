package com.potatorider.deliveryinfoservice.domain

import com.potatorider.domain.DeliveryStatus.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeliveryStatusTests {
    @Test
    fun get_next_is_not_last() {
        // Act + Assert
        assertThat(REQUEST.next).isEqualTo(ACCEPT)
    }

    @Test
    fun get_next_is_last() {
        // Act + Assert
        assertThat(COMPLETE.next).isEqualTo(COMPLETE)
    }
}
