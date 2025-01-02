package com.potatorider.repostiory

import com.potatorider.createInvalidDelivery
import com.potatorider.domain.ReceiverType
import com.potatorider.domain.RelayRequest
import com.potatorider.repository.RelayRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.test.StepVerifier

@DataMongoTest
@Testcontainers
class RelayRepositoryTests {
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
    fun find_all_by_receiver_type_containing() {
        // Arrange
        val relayRequestList = makeRequest()
        relayRepository.saveAll(relayRequestList).blockLast()
        val pageRequest = PageRequest.of(0, 10)

        // Act
        val result =
            relayRepository.findAllByReceiverTypeContaining(pageRequest, ReceiverType.SHOP)

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { request: RelayRequest -> request.receiverType == ReceiverType.SHOP }
            .expectNextMatches { request: RelayRequest -> request.receiverType == ReceiverType.SHOP }
            .expectNextMatches { request: RelayRequest -> request.receiverType == ReceiverType.SHOP }
            .verifyComplete()
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
