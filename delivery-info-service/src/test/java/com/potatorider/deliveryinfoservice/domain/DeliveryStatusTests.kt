package com.potatorider.deliveryinfoservice.domain;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.COMPLETE;
import static com.potatorider.domain.DeliveryStatus.REQUEST;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DeliveryStatusTests {

    @Test
    void get_next_is_not_last() {
        // Act + Assert
        assertThat(REQUEST.getNext()).isEqualTo(ACCEPT);
    }

    @Test
    void get_next_is_last() {
        // Act + Assert
        assertThat(COMPLETE.getNext()).isEqualTo(COMPLETE);
    }
}
