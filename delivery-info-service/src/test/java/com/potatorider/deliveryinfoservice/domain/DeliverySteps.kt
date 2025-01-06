package com.potatorider.deliveryinfoservice.domain

import com.potatorider.domain.Delivery
import com.potatorider.domain.DeliveryStatus
import java.time.LocalDateTime

fun makeValidDeliveryWithDeliveryStatus(deliveryStatus: DeliveryStatus?): Delivery {
    val id = "id-1234"
    val orderId = "order1"
    val riderId = "rider-1234"
    val agencyId = "agency-1234"
    val shopId = "shop-1234"
    val customerId = "customer-1234"
    val address = "충주시 호반로..."
    val phoneNumber = "01011112222"
    val orderTime = LocalDateTime.now().minusMinutes(1)
    val pickupTime = LocalDateTime.now().plusMinutes(10)
    val finishTime = LocalDateTime.now().plusMinutes(30)

    return Delivery(
        id,
        orderId,
        riderId,
        agencyId,
        shopId,
        customerId,
        address,
        phoneNumber,
        "",
        deliveryStatus,
        orderTime,
        pickupTime,
        finishTime
    )
}