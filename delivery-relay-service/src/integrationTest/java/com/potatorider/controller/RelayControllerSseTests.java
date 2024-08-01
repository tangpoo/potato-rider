package com.potatorider.controller;


import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import com.potatorider.domain.RelayRequest;
import com.potatorider.service.RelayService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RelayControllerSseTests {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RelayService relayService;

    @Test
    public void test_sse_end_point() {
        // Arrange
        String url = "http://localhost:" + port + "/api/v1/relay/stream";

        final Delivery delivery = createDelivery();

        relayService.saveDelivery(delivery).block();

        // Act
        Flux<ServerSentEvent<RelayRequest>> eventFlux =
            webTestClient
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
