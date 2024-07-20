package com.potatorider.repostiory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.ReceiverType;
import com.potatorider.domain.RelayRequest;
import com.potatorider.repository.RelayRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@DataMongoTest
@Testcontainers
public class RelayRepositoryTests {

    @Autowired RelayRepository relayRepository;

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
    void find_all_by_receiver_type_containing() {
        // Arrange
        final List<RelayRequest> relayRequestList = makeRequest();
        relayRepository.saveAll(relayRequestList).blockLast();
        final PageRequest pageRequest = PageRequest.of(0, 10);

        // Act
        var result =
                relayRepository.findAllByReceiverTypeContaining(pageRequest, ReceiverType.SHOP);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(request -> request.getReceiverType() == ReceiverType.SHOP)
                .expectNextMatches(request -> request.getReceiverType() == ReceiverType.SHOP)
                .expectNextMatches(request -> request.getReceiverType() == ReceiverType.SHOP)
                .verifyComplete();
    }
}