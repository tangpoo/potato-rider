package com.potatorider.config.swagger

import com.potatorider.domain.Delivery
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Tag(name = "Delivery API")
interface DeliveryControllerSwaggerDoc {
    @Operation(summary = "신규배송 저장")
    fun saveDelivery(delivery: Delivery): Mono<Delivery>

    @Operation(summary = "배송 승인")
    fun acceptDelivery(deliveryId: String): Mono<Delivery>

    @Operation(summary = "배차 승인")
    fun setDeliveryRider(deliveryId: String): Mono<Delivery>

    @Operation(summary = "상품 픽업")
    fun pickUpDelivery(deliveryId: String): Mono<Delivery>

    @Operation(summary = "배송 완료")
    fun completeDelivery(deliveryId: String): Mono<Delivery>

    @Operation(summary = "배송 조회")
    fun findDelivery(deliveryId: String): Mono<Delivery>

    @Operation(summary = "전체 배송 조회")
    fun findAllDelivery(page: Int, size: Int): Flux<Delivery>

    @Operation(summary = "배송 픽업 확인")
    fun isPickedUp(deliveryId: String): Mono<Boolean>
}
