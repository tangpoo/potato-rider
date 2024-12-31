package com.potatorider.controller

import com.potatorider.deliveryinfoservice.domain.DeliverySteps
import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import com.potatorider.publihser.DeliveryPublisher
import com.potatorider.repository.DeliveryRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
class DeliveryControllerTests @Autowired constructor(
    private var testClient: WebTestClient,
    private var deliveryRepository: DeliveryRepository,
    @SpyBean var deliveryPublisher: DeliveryPublisher
) {
    @BeforeEach
    fun setUp() {
        val setUpDatabase = deliveryRepository.deleteAll()
        StepVerifier.create(setUpDatabase).verifyComplete()
    }

    @DisplayName("신규배송저장")
    @Test
    fun save_delivery() {
        // Arrange
        val delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(null)

        // Act
        val result =
            testClient.post().uri("/api/v1/delivery").bodyValue(delivery).exchange().expectStatus()
                .isOk().expectBody(Delivery::class.java).returnResult().responseBody

        // Assert
        Assertions.assertThat(result.orderId).isEqualTo(delivery.orderId)
        Assertions.assertThat(result.deliveryStatus).isEqualTo(DeliveryStatus.REQUEST)
    }

    @DisplayName("배송승인")
    @Test
    fun accept_delivery() {
        // Arrange
        val delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(DeliveryStatus.REQUEST)
        val saveDelivery = deliveryRepository.save(delivery).block()

        // Act
        val result =
            testClient.put().uri("/api/v1/delivery/{deliveryId}/accept", saveDelivery.id).exchange()
                .expectStatus().isOk().expectBody(Delivery::class.java).returnResult().responseBody

        // Assert
        Assertions.assertThat(result.orderId).isEqualTo(delivery.orderId)
        Assertions.assertThat(result.deliveryStatus).isEqualTo(DeliveryStatus.ACCEPT)
    }

    companion object {
        @Container
        private val rabbitmqContainer = RabbitMQContainer("rabbitmq:latest")

        @Container
        private val mongoContainer = MongoDBContainer("mongodb/mongodb-community-server:latest")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            rabbitmqContainer.start()
            mongoContainer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configure(registry: DynamicPropertyRegistry) {
            registry.add("spring.rabbitmq.host") { rabbitmqContainer.host }
            registry.add("spring.rabbitmq.port") { rabbitmqContainer.amqpPort }
            registry.add("spring.data.mongodb.uri") { mongoContainer.replicaSetUrl }
        }
    }
}
