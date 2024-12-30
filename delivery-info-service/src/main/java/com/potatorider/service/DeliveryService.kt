package com.potatorider.service

import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import com.potatorider.exception.DeliveryNotFoundException
import com.potatorider.exception.RetryExhaustedException
import com.potatorider.publihser.DeliveryPublisher
import com.potatorider.repository.DeliveryRepository
import com.potatorider.util.statusExpectIs
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import reactor.util.retry.Retry.RetrySignal
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration
import java.util.concurrent.TimeoutException
import java.util.function.BiFunction

@Service
class DeliveryService(
    private val deliveryRepository: DeliveryRepository,
    private val deliveryPublisher: DeliveryPublisher
) {

    companion object {
        private const val MAX_ATTEMPTS = 3L
        private val FIXED_DELAY: Duration = Duration.ofMillis(500)
    }

    fun saveDelivery(delivery: Delivery): Mono<Delivery> = deliveryRepository
        .save(delivery.setDeliveryStatusRequest())
        .flatMap { del ->
            deliveryPublisher
                .sendAddDeliveryEvent(del)
                .retryWhen(retryBackoffSpec())
        }

    fun acceptDelivery(deliveryId: String): Mono<Delivery> = findDeliveryOrThrow(deliveryId)
        .flatMap { it.statusExpectIs(DeliveryStatus.REQUEST) }
        .map { it.nextStatus() }
        .flatMap(deliveryRepository::save)
        .flatMap { del ->
            deliveryPublisher
                .sendSetRiderEvent(del)
                .retryWhen(retryBackoffSpec())
        }

    fun setDeliveryRider(deliveryId: String): Mono<Delivery> = findDeliveryOrThrow(deliveryId)
        .flatMap { it.statusExpectIs(DeliveryStatus.ACCEPT) }
        .map { it.nextStatus() }
        .flatMap(deliveryRepository::save)

    fun pickUpDelivery(deliveryId: String): Mono<Delivery> = findDeliveryOrThrow(deliveryId)
        .flatMap { it.statusExpectIs(DeliveryStatus.RIDER_SET) }
        .map { it.nextStatus() }
        .map { it.setPickupTime() }
        .flatMap(deliveryRepository::save)

    fun completeDelivery(deliveryId: String): Mono<Delivery> = findDeliveryOrThrow(deliveryId)
        .flatMap { it.statusExpectIs(DeliveryStatus.PICKED_UP) }
        .map { it.nextStatus() }
        .map { it.setFinishTime() }
        .flatMap(deliveryRepository::save)

    fun findDelivery(deliveryId: String): Mono<Delivery> = findDeliveryOrThrow(deliveryId)

    fun findAllDelivery(page: Int, size: Int): Flux<Delivery> =
        PageRequest.of(page, size).let {
            deliveryRepository
                .findAllBy(it)
                .switchIfEmpty(Flux.error { DeliveryNotFoundException() })
        }

    fun isPickedUp(deliveryId: String): Mono<Boolean> = findDeliveryOrThrow(deliveryId)
        .flatMap { delivery -> Mono.just(delivery.deliveryStatus == DeliveryStatus.PICKED_UP) }
        .onErrorReturn(false)

    private fun retryBackoffSpec(): RetryBackoffSpec =
        Retry.fixedDelay(MAX_ATTEMPTS, FIXED_DELAY)
        .filter { it is TimeoutException }
        .onRetryExhaustedThrow { _, retrySignal ->
                RetryExhaustedException(retrySignal)
            }


    private fun findDeliveryOrThrow(deliveryId: String): Mono<Delivery> =
        deliveryRepository
            .findById(deliveryId)
            .switchIfEmpty(Mono.error { DeliveryNotFoundException() })
}
