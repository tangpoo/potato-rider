package com.potatorider.exception

import com.potatorider.controller.DeliveryController
import com.potatorider.domain.Delivery
import com.potatorider.service.DeliveryService
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.stream.Stream

@WebFluxTest(DeliveryController::class)
@AutoConfigureWebTestClient
class ExceptionHandlerAdviceTests @Autowired constructor(
    private var testClient: WebTestClient,
    @MockBean
    private var deliveryService: DeliveryService
) {

    private val DELIVERY_URL: String = "/api/v1/delivery"

    @ParameterizedTest
    @MethodSource("exceptionClassList")
    fun exceptionHandlingTest(throwable: Throwable, httpStatus: HttpStatus) {
        // Arrange
        val deliveryId = "id-1234"

        Mockito.`when`(deliveryService.findDelivery(deliveryId)).thenThrow(throwable)

        // Act + Assert
        testClient
            .get()
            .uri("$DELIVERY_URL/{deliveryId}", deliveryId)
            .exchange()
            .expectStatus()
            .isEqualTo(httpStatus)
    }

    @Test
    fun handleWebExchangeBindingError() {
        // Arrange
        // Act
        testClient
            .post()
            .uri(DELIVERY_URL)
            .bodyValue(createInvalidDelivery())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.BAD_REQUEST.value())

        // Assert
    }

    private fun createInvalidDelivery(): Delivery {
        return Delivery(
            orderId = "",
            shopId = "",
            customerId = "",
            address = "",
            phoneNumber = "",
            orderTime = LocalDateTime.now()
        )
    }

    companion object {
        @JvmStatic
        fun exceptionClassList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(IllegalArgumentException(), HttpStatus.BAD_REQUEST),
                Arguments.of(IllegalStateException(), HttpStatus.BAD_REQUEST),
                Arguments.of(DeliveryNotFoundException(), HttpStatus.BAD_REQUEST),
                Arguments.of(RuntimeException(), HttpStatus.INTERNAL_SERVER_ERROR)
            )
        }
    }
}
