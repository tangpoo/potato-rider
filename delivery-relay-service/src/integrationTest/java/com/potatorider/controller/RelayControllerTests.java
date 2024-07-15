package com.potatorider.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.ReceiverType;
import com.potatorider.domain.RelayRequest;
import com.potatorider.repository.RelayRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public class RelayControllerTests {

    @Autowired private WebTestClient testClient;

    @Autowired private RelayRepository relayRepository;

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongodb/mongodb-community-server:latest");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        relayRepository.deleteAll().block();
    }

    private List<RelayRequest> makeRequest() {

        List<RelayRequest> relayRequestList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            RelayRequest relayRequest1 =
                    new RelayRequest(ReceiverType.SHOP, "shop-" + i, new Delivery());
            RelayRequest relayRequest2 =
                    new RelayRequest(ReceiverType.AGENCY, "agency-" + i, new Delivery());
            relayRequestList.add(relayRequest1);
            relayRequestList.add(relayRequest2);
        }

        assertEquals(relayRequestList.size(), 6);

        return relayRequestList;
    }

    @Test
    void find_all_request() {
        // Arrange
        final List<RelayRequest> relayRequestList = makeRequest();
        relayRepository.saveAll(relayRequestList).blockLast();

        // Act
        var result =
                testClient
                        .get()
                        .uri("/api/v1/relay/shop")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBodyList(RelayRequest.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getReceiverType()).isEqualTo(ReceiverType.SHOP);
        assertThat(result.get(1).getReceiverType()).isEqualTo(ReceiverType.SHOP);
        assertThat(result.get(2).getReceiverType()).isEqualTo(ReceiverType.SHOP);
    }

    @Test
    void find_all_agency() {
        // Arrange
        final List<RelayRequest> relayRequestList = makeRequest();
        relayRepository.saveAll(relayRequestList).blockLast();

        // Act
        var result =
                testClient
                        .get()
                        .uri("/api/v1/relay/agency")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBodyList(RelayRequest.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getReceiverType()).isEqualTo(ReceiverType.AGENCY);
        assertThat(result.get(1).getReceiverType()).isEqualTo(ReceiverType.AGENCY);
        assertThat(result.get(2).getReceiverType()).isEqualTo(ReceiverType.AGENCY);
    }
}
