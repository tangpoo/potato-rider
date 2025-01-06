package com.potatorider.controller

import com.potatorider.domain.RiderLocation
import com.potatorider.service.RiderLocationService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/rider/location")
class RiderLocationController(private val riderLocationService: RiderLocationService) {

    @PostMapping
    fun updateLocationPost(@RequestBody riderLocation: RiderLocation): Mono<Boolean> =
        riderLocationService.tryPutOperation(riderLocation)

    @PutMapping
    fun updateLocationPut(@RequestBody riderLocation: RiderLocation): Mono<Boolean> =
        riderLocationService.tryPutOperation(riderLocation)

    @GetMapping("/{locationId}")
    fun getLocation(@PathVariable locationId: String): Mono<RiderLocation> =
        riderLocationService.getLocation(locationId)
}
