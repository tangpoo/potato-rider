package com.potatorider.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.potatorider.domain.RiderLocation;
import com.potatorider.domain.RiderLocationSteps;
import com.potatorider.repository.DeliveryRepositoryImpl;
import com.potatorider.repository.RiderLocationRepositoryImpl;
import com.potatorider.service.RiderLocationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@Testcontainers
public class RiderLocationControllerTests {

    @Container
    private static final GenericContainer<?> redisContainer =
        new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @Autowired
    private WebTestClient testClient;

    @Autowired private ReactiveRedisTemplate<String, RiderLocation> redisTemplate;

    @SpyBean
    private DeliveryRepositoryImpl deliveryRepository;

    private WireMockServer wireMockServer;

    String riderLocationMapping = "/api/v1/rider/location";

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        deliveryRepository.setUriDeliveryInfoService("http://127.0.0.1:" + wireMockServer.port());
    }

    @Test
    void update_location_post() {
        // Arrange
        final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();
        wireMockServer.stubFor(get(urlMatching("/api/v1/delivery/.*?/is-picked-up"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("true")));
        // Act
        var result = testClient
            .post()
            .uri(riderLocationMapping)
            .bodyValue(riderLocation)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Boolean.class)
            .getResponseBody();

        // Assert
        StepVerifier.create(result).expectNextMatches(Boolean::booleanValue).verifyComplete();
    }

    @Test
    void update_location_put() {
        // Arrange
        final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();

        redisTemplate.opsForValue().set(riderLocation.getId(), riderLocation).block();

        // Act
        var result = testClient
            .put()
            .uri(riderLocationMapping)
            .bodyValue(riderLocation)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Boolean.class)
            .getResponseBody();

        // Assert
        StepVerifier.create(result).expectNextMatches(Boolean::booleanValue).verifyComplete();
        verify(deliveryRepository, times(0)).isPickedUp(any());
    }

    @Test
    void get_location() {
        // Arrange
        final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();

        redisTemplate.opsForValue().set(riderLocation.getId(), riderLocation).block();

        // Act
        var result = testClient
            .get()
            .uri(riderLocationMapping + "/" + riderLocation.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(RiderLocation.class)
            .returnResult()
            .getResponseBody();

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(riderLocation.getId());
    }

}
