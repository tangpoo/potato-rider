package com.potatorider.config.swagger;

import com.potatorider.domain.Delivery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "Delivery API")
public interface DeliveryControllerSwaggerDoc {

  @Operation(summary = "신규배달 저장")
  Mono<Delivery> saveDelivery(Delivery delivery);
}
