package com.potatorider.exception;

import static org.mockito.Mockito.when;

import com.potatorider.controller.DeliveryController;
import com.potatorider.domain.Delivery;
import com.potatorider.service.DeliveryService;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(DeliveryController.class)
@AutoConfigureWebTestClient
public class ExceptionHandlerAdviceTests {

    @Autowired
    WebTestClient testClient;

    @MockBean
    DeliveryService deliveryService;

    final String DELIVERY_URL = "/api/v1/delivery";

    static Stream<Arguments> exceptionClassList() {
        return Stream.of(
            Arguments.of(new IllegalArgumentException(), HttpStatus.BAD_REQUEST),
            Arguments.of(new IllegalStateException(), HttpStatus.BAD_REQUEST),
            Arguments.of(new DeliveryNotFoundException(), HttpStatus.BAD_REQUEST),
            Arguments.of(new RuntimeException(), HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionClassList")
    void exceptionHandlingTest(Throwable throwable, HttpStatus httpStatus) {
        // Arrange
        String deliveryId = "id-1234";

        when(deliveryService.findDelivery(deliveryId)).thenThrow(throwable);

        // Act + Assert
        testClient
            .get()
            .uri(DELIVERY_URL + "/{deliveryId}", deliveryId)
            .exchange()
            .expectStatus()
            .isEqualTo(httpStatus);
    }

    @Test
    void handleWebExchangeBindingError() {
        // Arrange
        // Act
        testClient
            .post()
            .uri(DELIVERY_URL)
            .bodyValue(new Delivery())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST.value());

        // Assert
    }

}
