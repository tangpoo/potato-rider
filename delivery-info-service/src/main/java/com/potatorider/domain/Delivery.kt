package com.potatorider.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
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
    var deliveryStatus: DeliveryStatus? = null,
    val orderTime: LocalDateTime,
    var pickupTime: LocalDateTime? = null,
    var finishTime: LocalDateTime? = null
) {
    fun setDeliveryStatusRequest(): Delivery {
        deliveryStatus = DeliveryStatus.REQUEST
        return this
    }

    fun nextStatus(): Delivery {
        deliveryStatus = deliveryStatus?.next
        return this
    }

    fun setPickupTime(): Delivery {
        pickupTime = LocalDateTime.now()
        return this
    }

    fun setFinishTime(): Delivery {
        finishTime = LocalDateTime.now()
        return this
    }
}
