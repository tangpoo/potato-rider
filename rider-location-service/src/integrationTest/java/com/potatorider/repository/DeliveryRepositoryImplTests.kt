package com.potatorider.repository

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import reactor.test.StepVerifier
import java.util.stream.Stream

@AutoConfigureWireMock(port = 0)
@ExtendWith(MockitoExtension::class)
class DeliveryRepositoryImplTests {

    @SpyBean
    private lateinit var deliveryRepository: DeliveryRepositoryImpl

    lateinit var wireMockServer: WireMockServer

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()
        deliveryRepository = DeliveryRepositoryImpl(WebClient.builder())
        deliveryRepository.uriDeliveryInfoService = "http://127.0.0.1:" + wireMockServer.port()
    }

    @ParameterizedTest
    @MethodSource("isPickedUpParameters")
    @Throws(
        JsonProcessingException::class
    )
    fun isPickedUp_test(isPickedUp: Boolean) {
        // Arrange
        val deliveryId = "delivery-1234"

        // Act
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlMatching("/api/v1/delivery/$deliveryId/is-picked-up"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(
                            HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE
                        )
                        .withBody(mapper.writeValueAsString(isPickedUp))
                        .withStatus(HttpStatus.OK.value())
                )
        )

        val result = deliveryRepository.isPickedUp(deliveryId)

        // Assert
        StepVerifier.create(result).expectNext(isPickedUp).verifyComplete()
    }

    companion object {
        @JvmStatic
        val isPickedUpParameters: Stream<Arguments>
            get() = Stream.of(Arguments.of(true), Arguments.of(false))
    }
}
