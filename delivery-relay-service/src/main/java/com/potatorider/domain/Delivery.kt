package com.potatorider.domain

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class Delivery(
    @Id val id: String? = null,
    val orderId: String,
    val riderId: String? = null,
    val agencyId: String? = null,
    val shopId: String,
    val customerId: String,
    val address: String,
    val phoneNumber: String,
    val comment: String? = null,
    val deliveryStatus: DeliveryStatus? = null,
    val orderTime: LocalDateTime,
    val pickupTime: LocalDateTime? = null,
    val finishTime: LocalDateTime? = null
)