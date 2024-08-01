package com.potatorider.controller;


import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import com.potatorider.domain.RelayRequest;
import com.potatorider.repository.RelayRepository;
import com.potatorider.service.RelayService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RelayControllerSseTests {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private RelayService relayService;

    @Autowired
    private RelayRepository relayRepository;

    @Container
    private static final RabbitMQContainer rabbitmqContainer =
        new RabbitMQContainer("rabbitmq:latest");

    @Container
    private static final MongoDBContainer mongoContainer =
        new MongoDBContainer("mongodb/mongodb-community-server:latest");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void beforeAll() {
        rabbitmqContainer.start();
        mongoContainer.start();
    }

    @AfterEach
    void tearDown() {
        relayRepository.deleteAll().block();
    }

    @Test
    public void test_sse_end_point() {
        // Arrange
        String url = "http://localhost:" + port + "/api/v1/relay/stream";

        final Delivery delivery = createDelivery();

        relayService.saveDelivery(delivery).block();

        // Act
        Flux<ServerSentEvent<RelayRequest>> eventFlux =
            testClient
                .get()
                .uri(url)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<RelayRequest>>() {})
                .getResponseBody();

        // Assert
        StepVerifier.create(eventFlux)
            .expectNextMatches(event -> {
                RelayRequest relayRequest = event.data();
                return relayRequest != null && relayRequest.getDelivery().getOrderId().equals(delivery.getOrderId());
            })
            .thenCancel()
            .verify();
    }

    private Delivery createDelivery() {
        String id = "id-1234";
        String orderId = "order1";
        String riderId = "rider-1234";
        String agencyId = "agency-1234";
        String shopId = "shop-1234";
        String customerId = "customer-1234";
        String address = "충주시 호반로...";
        String phoneNumber = "01011112222";
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(1);
        LocalDateTime pickupTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime finishTime = LocalDateTime.now().plusMinutes(30);

        return new Delivery(
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
            finishTime);
    }
}
