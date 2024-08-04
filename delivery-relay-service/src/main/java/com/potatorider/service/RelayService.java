package com.potatorider.service;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.ReceiverType;
import com.potatorider.domain.RelayRequest;
import com.potatorider.repository.RelayRepository;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelayService {

    private final RelayRepository relayRepository;
    private final Map<String, Many<RelayRequest>> relayRequestSinkMap = new ConcurrentHashMap<>();
    private final Map<String, Many<String>> notAcceptedSinkMap = new ConcurrentHashMap<>();

    public Mono<RelayRequest> saveDelivery(final Delivery delivery, ReceiverType receiverType) {
        RelayRequest relayRequest = new RelayRequest(receiverType, delivery.getShopId(), delivery);
        return relayRepository.save(relayRequest)
            .doOnNext(request -> {
                Sinks.Many<RelayRequest> sink = relayRequestSinkMap.computeIfAbsent(
                    request.getReceiverId(),
                    key -> Sinks.many().replay().all());
                sink.tryEmitNext(request);
            });
    }

    public Flux<RelayRequest> findAllByShop(int page, int size) {
        final Pageable pageable = PageRequest.of(page, size);
        return relayRepository.findAllByReceiverTypeContaining(pageable, ReceiverType.SHOP);
    }

    public Flux<RelayRequest> findAllByAgency(int page, int size) {
        final Pageable pageable = PageRequest.of(page, size);
        return relayRepository.findAllByReceiverTypeContaining(pageable, ReceiverType.AGENCY);
    }

    public Flux<ServerSentEvent<RelayRequest>> streamRelayRequests(final String lastEventId,
        final String receiverId) {
        Sinks.Many<RelayRequest> requestSink = relayRequestSinkMap.get(receiverId);

        Flux<RelayRequest> relayRequestFlux = requestSink.asFlux();

        if (!lastEventId.isEmpty()) {
            relayRequestFlux =
                relayRequestFlux.filter(
                    relayRequest -> relayRequest.getId().compareTo(lastEventId) > 0);
        }

        return relayRequestFlux
            .map(data -> ServerSentEvent.builder(data).build())
            .onErrorResume(
                e -> {
                    log.error("Error occurred in SSE stream", e);
                    return Flux.empty();
                })
            .timeout(Duration.ofMinutes(2))
            .retry(3);
    }

    public Flux<ServerSentEvent<String>> streamAlert(final String receiverId) {
        Sinks.Many<String> alertSink = notAcceptedSinkMap.get(receiverId);

        Flux<String> alertFlux = alertSink.asFlux();

        return alertFlux
            .map(data -> ServerSentEvent.builder(data).build())
            .onErrorResume(
                e -> {
                    log.error("Error can not send alert", e);
                    return Flux.empty();
                })
            .timeout(Duration.ofMinutes(2))
            .retry(3);
    }

    @Scheduled(fixedRate = 300000)
    public void sendAlert() {
        log.info("send alert");
        relayRepository.findAllByIsAcceptedAndIsEnabled(false, true)
            .doOnNext(relayRequest -> {
                Sinks.Many<String> sink = notAcceptedSinkMap.computeIfAbsent(
                    relayRequest.getReceiverId(),
                    key -> Sinks.many().replay().all());
                sink.tryEmitNext(relayRequest.getId());
            })
            .subscribe();
    }
}
