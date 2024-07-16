package com.potatorider.controller;

import static com.potatorider.domain.DeliveryStatus.ACCEPT;
import static com.potatorider.domain.DeliveryStatus.REQUEST;

import static org.assertj.core.api.Assertions.assertThat;

import com.potatorider.deliveryinfoservice.domain.DeliverySteps;
import com.potatorider.domain.Delivery;
import com.potatorider.publihser.DeliveryPublisher;
import com.potatorider.repository.DeliveryRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
public class DeliveryControllerTests {

    @Autowired WebTestClient testClient;

    @Autowired DeliveryRepository deliveryRepository;

    @SpyBean DeliveryPublisher deliveryPublisher;

    @Container
    private static final RabbitMQContainer rabbitmqContainer =
            new RabbitMQContainer("rabbitmq:latest");

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongodb/mongodb-community-server:latest");

    @BeforeAll
    static void beforeAll() {
        rabbitmqContainer.start();
        mongoContainer.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitmqContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitmqContainer::getAmqpPort);
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        var setUpDatabase = deliveryRepository.deleteAll();
        StepVerifier.create(setUpDatabase).verifyComplete();
    }

    @DisplayName("신규배송저장")
    @Test
    void save_delivery() {
        // Arrange
        var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(null);

        // Act
        var result =
                testClient
                        .post()
                        .uri("/api/v1/delivery")
                        .bodyValue(delivery)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Delivery.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertThat(result.getOrderId()).isEqualTo(delivery.getOrderId());
        assertThat(result.getDeliveryStatus()).isEqualTo(REQUEST);
    }

    @DisplayName("배송승인")
    @Test
    void accept_delivery() {
        // Arrange
        var delivery = DeliverySteps.makeValidDeliveryWithDeliveryStatus(REQUEST);
        var saveDelivery = deliveryRepository.save(delivery).block();

        // Act
        var result =
                testClient
                        .put()
                        .uri("/api/v1/delivery/{deliveryId}/accept", saveDelivery.getId())
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Delivery.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertThat(result.getOrderId()).isEqualTo(delivery.getOrderId());
        assertThat(result.getDeliveryStatus()).isEqualTo(ACCEPT);
    }
}
