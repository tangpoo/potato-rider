package com.potatorider.controller

import com.potatorider.createInvalidDelivery
import com.potatorider.domain.ReceiverType
import com.potatorider.domain.RelayRequest
import com.potatorider.repository.RelayRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class RelayControllerTests {
    @Autowired
    lateinit var testClient: WebTestClient

    @Autowired
    lateinit var relayRepository: RelayRepository

    @AfterEach
    fun tearDown() {
        relayRepository.deleteAll().block()
    }

    private fun makeRequest(): List<RelayRequest> {
        val relayRequestList: MutableList<RelayRequest> = ArrayList()

        for (i in 0..2) {
            val relayRequest1 =
                RelayRequest(ReceiverType.SHOP, "shop-$i", createInvalidDelivery())
            val relayRequest2 =
                RelayRequest(ReceiverType.AGENCY, "agency-$i", createInvalidDelivery())
            relayRequestList.add(relayRequest1)
            relayRequestList.add(relayRequest2)
        }

        Assertions.assertEquals(relayRequestList.size, 6)

        return relayRequestList
    }

    @Test
    fun find_all_request() {
        // Arrange
        val relayRequestList = makeRequest()
        relayRepository.saveAll(relayRequestList).blockLast()

        // Act
        val result =
            testClient
                .get()
                .uri("/api/v1/relay/shop")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RelayRequest::class.java)
                .returnResult()
                .responseBody

        // Assert
        assertThat(result!!.size).isEqualTo(3)
        assertThat(result[0].receiverType)
            .isEqualTo(ReceiverType.SHOP)
        assertThat(result[1].receiverType)
            .isEqualTo(ReceiverType.SHOP)
        assertThat(result[2].receiverType)
            .isEqualTo(ReceiverType.SHOP)
    }

    @Test
    fun find_all_agency() {
        // Arrange
        val relayRequestList = makeRequest()
        relayRepository.saveAll(relayRequestList).blockLast()

        // Act
        val result =
            testClient
                .get()
                .uri("/api/v1/relay/agency")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RelayRequest::class.java)
                .returnResult()
                .responseBody

        // Assert
        assertThat(result!!.size).isEqualTo(3)
        assertThat(result[0].receiverType)
            .isEqualTo(ReceiverType.AGENCY)
        assertThat(result[1].receiverType)
            .isEqualTo(ReceiverType.AGENCY)
        assertThat(result[2].receiverType)
            .isEqualTo(ReceiverType.AGENCY)
    }


    companion object {
        @Container
        @JvmStatic
        private val mongoContainer = MongoDBContainer("mongodb/mongodb-community-server:latest")

        @DynamicPropertySource
        @JvmStatic
        fun configure(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") { mongoContainer.replicaSetUrl }
        }
    }
}
