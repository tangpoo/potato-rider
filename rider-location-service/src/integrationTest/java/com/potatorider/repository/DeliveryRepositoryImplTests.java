package com.potatorider.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import reactor.test.StepVerifier;

import java.util.stream.Stream;

@AutoConfigureWireMock(port = 0)
@ExtendWith(MockitoExtension.class)
public class DeliveryRepositoryImplTests {

    @InjectMocks private DeliveryRepositoryImpl deliveryRepository;

    private WireMockServer wireMockServer;

    private ObjectMapper mapper = new ObjectMapper();

    static Stream<Arguments> isPickedUpParameters() {
        return Stream.of(Arguments.of(true), Arguments.of(false));
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        deliveryRepository.setUriDeliveryInfoService("http://127.0.0.1:" + wireMockServer.port());
    }

    @ParameterizedTest
    @MethodSource("isPickedUpParameters")
    void isPickedUp_test(Boolean isPickedUp) throws JsonProcessingException {
        // Arrange
        var deliveryId = "delivery-1234";

        // Act
        wireMockServer.stubFor(
                get(urlMatching("/api/v1/delivery/" + deliveryId + "/is-picked-up"))
                        .willReturn(
                                aResponse()
                                        .withHeader(
                                                HttpHeaders.CONTENT_TYPE,
                                                MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(mapper.writeValueAsString(isPickedUp))
                                        .withStatus(HttpStatus.OK.value())));

        var result = deliveryRepository.isPickedUp(deliveryId);

        // Assert
        StepVerifier.create(result).expectNext(isPickedUp).verifyComplete();
    }
}
