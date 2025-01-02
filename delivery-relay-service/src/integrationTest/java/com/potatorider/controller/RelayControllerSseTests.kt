package com.potatorider.controller

import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import com.potatorider.domain.ReceiverType
import com.potatorider.domain.RelayRequest
import com.potatorider.repository.RelayRepository
import com.potatorider.service.RelayService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.LocalDateTime

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class RelayControllerSseTests @Autowired constructor(
    private var testClient: WebTestClient,
    private var relayService: RelayService,
    private var relayRepository: RelayRepository
) {
    @LocalServerPort
    private val port = 0

    @AfterEach
    fun tearDown() {
        relayRepository.deleteAll().block()
    }

    @Test
    fun test_sse_end_point() {
        // Arrange
        val url = "http://localhost:$port/api/v1/relay/stream"
        val receiverId = "shop-1234"

        val delivery = createDelivery()

        relayService.saveDelivery(delivery, ReceiverType.SHOP).block()

        // Act
        val eventFlux: Flux<ServerSentEvent<RelayRequest>> =
            testClient
                .get()
                .uri(url)
                .header("Receiver-ID", receiverId)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult<ServerSentEvent<RelayRequest>>(
                    object : ParameterizedTypeReference<ServerSentEvent<RelayRequest>>() {})
                .responseBody


        // Assert
        StepVerifier.create(eventFlux)
            .expectNextMatches { event: ServerSentEvent<RelayRequest> ->
                val relayRequest = event.data()
                (relayRequest != null
                        && (relayRequest
                    .delivery
                    .orderId
                        == delivery.orderId))
            }
            .thenCancel()
            .verify()
    }

    private fun createDelivery(): Delivery {
        val id = "id-1234"
        val orderId = "order1"
        val riderId = "rider-1234"
        val agencyId = "agency-1234"
        val shopId = "shop-1234"
        val customerId = "customer-1234"
        val address = "충주시 호반로..."
        val phoneNumber = "01011112222"
        val orderTime = LocalDateTime.now().minusMinutes(1)
        val pickupTime = LocalDateTime.now().plusMinutes(10)
        val finishTime = LocalDateTime.now().plusMinutes(30)

        return Delivery(
            id,
            orderId,
            riderId,
            agencyId,
            shopId,
            customerId,
            address,
            phoneNumber,
            "",
            DeliveryStatus.REQUEST,
            orderTime,
            pickupTime,
            finishTime
        )
    }

    companion object {
        @Container
        private val rabbitmqContainer = RabbitMQContainer("rabbitmq:latest")

        @Container
        private val mongoContainer = MongoDBContainer("mongodb/mongodb-community-server:latest")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            rabbitmqContainer.start()
            mongoContainer.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun configure(registry: DynamicPropertyRegistry) {
            registry.add("spring.rabbitmq.host") { rabbitmqContainer.host }
            registry.add("spring.rabbitmq.port") { rabbitmqContainer.amqpPort }
            registry.add("spring.data.mongodb.uri") { mongoContainer.replicaSetUrl }
        }

    }
}
