package com.potatorider.controller;

import com.potatorider.domain.RiderLocation;
import com.potatorider.service.RiderLocationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rider/location")
public class RiderLocationController {

    private final RiderLocationService riderLocationService;

    @PostMapping
    public Mono<Boolean> updateLocationPost(@RequestBody RiderLocation riderLocation) {
        return riderLocationService.tryPutOperation(riderLocation);
    }

    @PutMapping
    public Mono<Boolean> updateLocationPut(@RequestBody RiderLocation riderLocation) {
        return riderLocationService.tryPutOperation(riderLocation);
    }

    @GetMapping("/{locationId}")
    public Mono<RiderLocation> getLocation(@PathVariable String locationId) {
        return riderLocationService.getLocation(locationId);
    }
}
