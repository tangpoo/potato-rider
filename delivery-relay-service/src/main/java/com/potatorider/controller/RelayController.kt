package com.potatorider.controller

import com.potatorider.domain.RelayRequest
import com.potatorider.service.RelayService
import lombok.extern.slf4j.Slf4j
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@Slf4j
@RequestMapping("/api/v1/relay")
class RelayController(private val relayService: RelayService) {

//    @GetMapping("/shop")
//    fun findAllRequest(
//        @RequestParam(defaultValue = "0") page: Int,
//        @RequestParam(defaultValue = "10") size: Int
//    ): Flux<RelayRequest> {
//        return relayService.findAllByShop(page, size)
//    }

    @GetMapping("/agency")
    fun findAllAgency(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Flux<RelayRequest> {
        return relayService.findAllByAgency(page, size)
    }

    @GetMapping(value = ["/stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamRelayRequests(
        @RequestHeader(value = "Last-Event-ID") lastEventId: String?,
        @RequestHeader(value = "Receiver-ID", required = true) receiverId: String
    ): Flux<ServerSentEvent<RelayRequest>> {
        return relayService.streamRelayRequests(lastEventId, receiverId)
    }

    @GetMapping(value = ["/stream/alert"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamAlert(
        @RequestHeader(value = "Receiver-ID", required = true) receiverId: String
    ): Flux<ServerSentEvent<String>> {
        return relayService.streamAlert(receiverId)
    }
}
