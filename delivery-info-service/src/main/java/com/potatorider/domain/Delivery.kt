package com.potatorider.domain

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Delivery(
    @Id val id: String? = null,
    @field:NotBlank val orderId: String,
    val riderId: String? = null,
    val agencyId: String? = null,
    @field:NotBlank val shopId: String,
    @field:NotBlank val customerId: String,
    @field:NotBlank val address: String,
    @field:NotBlank val phoneNumber: String,
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
