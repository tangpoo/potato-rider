package com.potatorider.deliveryinfoservice.domain;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.COMPLETE;
import static com.potatorider.domain.DeliveryStatus.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import com.potatorider.domain.DeliveryStatus;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
