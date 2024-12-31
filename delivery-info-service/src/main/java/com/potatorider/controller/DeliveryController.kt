package com.potatorider.controller

import com.potatorider.config.swagger.DeliveryControllerSwaggerDoc
import com.potatorider.domain.Delivery
import com.potatorider.service.DeliveryService
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@Slf4j
class DeliveryController(private val deliveryService: DeliveryService) : DeliveryControllerSwaggerDoc {

    @PostMapping
    override fun saveDelivery(@RequestBody @Valid delivery: Delivery): Mono<Delivery> {
        return deliveryService.saveDelivery(delivery)
    }

    @PutMapping("/{deliveryId}/accept")
    override fun acceptDelivery(@PathVariable deliveryId: String): Mono<Delivery> {
        return deliveryService.acceptDelivery(deliveryId)
    }

    @PutMapping("/{deliveryId}/rider")
    override fun setDeliveryRider(@PathVariable deliveryId: String): Mono<Delivery> {
        return deliveryService.setDeliveryRider(deliveryId)
    }

    @PutMapping("/{deliveryId}/pickup")
    override fun pickUpDelivery(@PathVariable deliveryId: String): Mono<Delivery> {
        return deliveryService.pickUpDelivery(deliveryId)
    }

    @PutMapping("/{deliveryId}/complete")
    override fun completeDelivery(@PathVariable deliveryId: String): Mono<Delivery> {
        return deliveryService.completeDelivery(deliveryId)
    }

    @GetMapping("/{deliveryId}")
    override fun findDelivery(@PathVariable deliveryId: String): Mono<Delivery> {
        return deliveryService.findDelivery(deliveryId)
    }

    @GetMapping
    override fun findAllDelivery(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "1") size: Int
    ): Flux<Delivery> {
        return deliveryService.findAllDelivery(page, size)
    }

    @GetMapping("/{deliveryId}/is-picked-up")
    override fun isPickedUp(@PathVariable deliveryId: String): Mono<Boolean> {
        return deliveryService.isPickedUp(deliveryId)
    }
}
