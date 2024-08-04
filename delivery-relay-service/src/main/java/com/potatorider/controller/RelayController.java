package com.potatorider.controller;

import com.potatorider.domain.RelayRequest;
import com.potatorider.service.RelayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/relay")
public class RelayController {

    private final RelayService relayService;

    @GetMapping("/shop")
    public Flux<RelayRequest> findAllRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return relayService.findAllByShop(page, size);
    }

    @GetMapping("/agency")
    public Flux<RelayRequest> findAllAgency(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return relayService.findAllByAgency(page, size);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<RelayRequest>> streamRelayRequests(
            @RequestHeader(value = "Last-Event-ID", defaultValue = "") String lastEventId,
            @RequestHeader(value = "Receiver-ID", required = false) String receiverId) {
        return relayService.streamRelayRequests(lastEventId, receiverId);
    }
}
