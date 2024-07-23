package com.potatorider.config.swagger;

import com.potatorider.domain.Delivery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Delivery API")
public interface DeliveryControllerSwaggerDoc {

    @Operation(summary = "신규배송 저장")
    Mono<Delivery> saveDelivery(Delivery delivery);

    @Operation(summary = "배송 승인")
    Mono<Delivery> acceptDelivery(String deliveryId);

    @Operation(summary = "배차 승인")
    Mono<Delivery> setDeliveryRider(String deliveryId);

    @Operation(summary = "상품 픽업")
    Mono<Delivery> pickUpDelivery(String deliveryId);

    @Operation(summary = "배송 완료")
    Mono<Delivery> completeDelivery(String deliveryId);

    @Operation(summary = "배송 조회")
    Mono<Delivery> findDelivery(String deliverId);

    @Operation(summary = "전체 배송 조회")
    Flux<Delivery> findAllDelivery(int page, int size);

    @Operation(summary = "배송 픽업 확인")
    Mono<Boolean> DeliveryIsPickedUp(String deliveryId);
}
