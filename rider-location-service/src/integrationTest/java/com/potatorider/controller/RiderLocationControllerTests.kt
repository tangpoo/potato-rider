package com.potatorider.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.potatorider.domain.RiderLocation
import com.potatorider.domain.createRiderLocation
import com.potatorider.repository.DeliveryRepositoryImpl
import com.potatorider.repository.RiderLocationRepositoryImpl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@Testcontainers
class RiderLocationControllerTests @Autowired constructor(
    private val testClient: WebTestClient,
    private val redisTemplate: ReactiveRedisTemplate<String, RiderLocation>,
) {
    private lateinit var deliveryRepository: DeliveryRepositoryImpl

    private lateinit var riderLocationRepository: RiderLocationRepositoryImpl

    private lateinit var wireMockServer: WireMockServer

    private val riderLocationMapping = "/api/v1/rider/location"

    @BeforeEach
    fun setUp() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()

        val actualRepository = DeliveryRepositoryImpl(WebClient.builder())
        actualRepository.uriDeliveryInfoService = "http://127.0.0.1:" + wireMockServer.port()
        deliveryRepository = Mockito.spy(actualRepository)
    }

    @Test
    fun update_location_post() {
        // Arrange
        val riderLocation = createRiderLocation()
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlMatching("/api/v1/delivery/.*?/is-picked-up"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                )
        )
        // Act
        val result =
            testClient
                .post()
                .uri(riderLocationMapping)
                .bodyValue(riderLocation)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Boolean::class.java)
                .responseBody

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { it }
            .verifyComplete()
    }

    @Test
    fun update_location_put() {
        // Arrange
        val riderLocation = createRiderLocation()

        redisTemplate.opsForValue().set(riderLocation.id, riderLocation).block()

        // Act
        val result =
            testClient
                .put()
                .uri(riderLocationMapping)
                .bodyValue(riderLocation)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Boolean::class.java)
                .responseBody

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { it }
            .verifyComplete()
        Mockito.verify(deliveryRepository, Mockito.times(0)).isPickedUp(any())
    }

    @Test
    fun get_location() {
        // Arrange
        val riderLocation = createRiderLocation()

        redisTemplate.opsForValue().set(riderLocation.id, riderLocation).block()

        // Act
        val result =
            testClient
                .get()
                .uri(riderLocationMapping + "/" + riderLocation.id)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(RiderLocation::class.java)
                .returnResult()
                .responseBody

        // Assert
        Assertions.assertThat(result.id).isEqualTo(riderLocation.id)
    }

    companion object {
        @Container
        private val redisContainer: GenericContainer<*> =
            GenericContainer<Nothing>(DockerImageName.parse("redis:latest")).withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.firstMappedPort }
        }
    }

}
