package com.potatorider.service

import com.potatorider.domain.Delivery
import com.potatorider.domain.ReceiverType
import com.potatorider.domain.RelayRequest
import com.potatorider.repository.RelayRepository
import com.potatorider.subscriber.DeliveryMessageSubscriber
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.codec.ServerSentEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class RelayService(private val relayRepository: RelayRepository) {

    private val relayRequestSinkMap = ConcurrentHashMap<String, Many<RelayRequest>>();
    private val notAcceptedSinkMap = ConcurrentHashMap<String, Many<String>>();

    private companion object {
        private val log: Logger = LoggerFactory.getLogger(DeliveryMessageSubscriber::class.java)
        private const val ALERT_INTERVAL = 300_000L
    }

    fun saveDelivery(delivery: Delivery, receiverType: ReceiverType): Mono<RelayRequest> {
        val relayRequest = RelayRequest(
            receiverType,
            delivery.shopId,
            delivery
        )
        return relayRepository.save(relayRequest)
            .doOnNext { request ->
                relayRequestSinkMap.computeIfAbsent(
                    request.receiverId
                ) {
                    Sinks.many().replay().all()
                }
                    .tryEmitNext(request)
            }
    }

    fun findAllByShop(page: Int, size: Int): Flux<RelayRequest> =
        relayRepository.findAllByReceiverTypeContaining(PageRequest.of(page, size), ReceiverType.SHOP)

    fun findAllByAgency(page: Int, size: Int): Flux<RelayRequest> =
        relayRepository.findAllByReceiverTypeContaining(PageRequest.of(page, size), ReceiverType.AGENCY)

    // 조회되지 않은 요청 실시간 갱신
    fun streamRelayRequests(
        lastEventId: String?, receiverId: String
    ): Flux<ServerSentEvent<RelayRequest>> {
        val requestSink = relayRequestSinkMap[receiverId] ?: return Flux.empty()

        val relayRequestFlux = requestSink.asFlux().apply {
            lastEventId?.let {
                filter { (it.id?.compareTo(lastEventId) ?: -1) > 0 }
            }
        }

        return relayRequestFlux
            .map<ServerSentEvent<RelayRequest>> {
                ServerSentEvent.builder(it).build()
            }
            .onErrorResume {
                log.error("Error occurred in SSE stream", it)
                Flux.empty()
            }
            .timeout(Duration.ofMinutes(2))
            .retry(3)
    }

    // 승인 or 거절되지 않은 요청 실시간 알림
    fun streamAlert(receiverId: String): Flux<ServerSentEvent<String>> {
        val alertSink = notAcceptedSinkMap[receiverId] ?: return Flux.empty()

        return alertSink.asFlux()
            .map<ServerSentEvent<String>> {
                ServerSentEvent.builder(it).build()
            }
            .onErrorResume {
                log.error("Error can not send alert", it)
                Flux.empty()
            }
            .timeout(Duration.ofMinutes(2))
            .retry(3)
    }

    // 승인 or 거절되지 않은 요청 3분 간격 알림
    @Scheduled(fixedRate = ALERT_INTERVAL)
    fun sendAlert() {
        log.info("send alert")
        relayRepository
            .findAllByIsAcceptedAndIsEnabled(false, true)
            .doOnNext { relayRequest ->
                notAcceptedSinkMap.computeIfAbsent(
                    relayRequest.receiverId
                ) { Sinks.many().replay().all() }
                    .tryEmitNext(relayRequest.id)
            }
            .subscribe()
    }
}
