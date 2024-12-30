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
import java.util.function.Supplier

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

    fun acceptDelivery(deliveryId: String): Mono<Delivery> = deliveryRepository
        .findById(deliveryId)
        .switchIfEmpty(Mono.error { DeliveryNotFoundException() })
        .flatMap { delivery ->
            delivery
                .statusExpectIs(DeliveryStatus.REQUEST)
        }
        .map { delivery -> delivery.nextStatus() }
        .flatMap { entity -> deliveryRepository.save(entity) }
        .flatMap { del ->
            deliveryPublisher
                .sendSetRiderEvent(del)
                .retryWhen(retryBackoffSpec())
        }

    fun setDeliveryRider(deliveryId: String): Mono<Delivery> = deliveryRepository
        .findById(deliveryId)
        .switchIfEmpty(Mono.error { DeliveryNotFoundException() })
        .flatMap { delivery ->
            delivery
                .statusExpectIs(DeliveryStatus.ACCEPT)
        }
        .map { delivery -> delivery.nextStatus() }
        .flatMap { entity -> deliveryRepository.save(entity) }

    fun pickUpDelivery(deliveryId: String): Mono<Delivery> = deliveryRepository
        .findById(deliveryId)
        .switchIfEmpty(Mono.error { DeliveryNotFoundException() })
        .flatMap { delivery ->
            delivery
                .statusExpectIs(DeliveryStatus.RIDER_SET)
        }
        .map { delivery -> delivery.nextStatus() }
        .map { delivery -> delivery.setPickupTime() }
        .flatMap { entity -> deliveryRepository.save(entity) }

    fun completeDelivery(deliveryId: String): Mono<Delivery> = deliveryRepository
        .findById(deliveryId)
        .switchIfEmpty(Mono.error { DeliveryNotFoundException() })
        .flatMap { delivery ->
            delivery.statusExpectIs(DeliveryStatus.PICKED_UP)
                .map { delivery.nextStatus() }
                .map { delivery.setFinishTime() }
                .flatMap { entity -> deliveryRepository.save(entity) }
        }

    fun findDelivery(deliveryId: String): Mono<Delivery> = deliveryRepository
        .findById(deliveryId)
        .switchIfEmpty(Mono.error { DeliveryNotFoundException() })

    fun findAllDelivery(page: Int, size: Int): Flux<Delivery> {
        val pageable: Pageable = PageRequest.of(page, size)
        return deliveryRepository
            .findAllBy(pageable)
            .switchIfEmpty(Flux.error { DeliveryNotFoundException() })
    }

    private fun retryBackoffSpec(): RetryBackoffSpec = Retry.fixedDelay(MAX_ATTEMPTS, FIXED_DELAY)
        .filter { ex: Throwable -> ex is TimeoutException }
        .onRetryExhaustedThrow(
            ((BiFunction { retryBackoffSpec: RetryBackoffSpec, retrySignal: RetrySignal ->
                RetryExhaustedException(
                    retrySignal
                )
            }))
        )

    fun isPickedUp(deliveryId: String): Mono<Boolean> = deliveryRepository
        .findById(deliveryId)
        .switchIfEmpty(Mono.error { DeliveryNotFoundException() })
        .flatMap { delivery -> Mono.just(delivery.deliveryStatus == DeliveryStatus.PICKED_UP) }
        .onErrorReturn(false)
}
