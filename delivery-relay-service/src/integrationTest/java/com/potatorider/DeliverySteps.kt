package com.potatorider

import com.potatorider.domain.Delivery
import java.time.LocalDateTime

fun createInvalidDelivery(): Delivery {
    return Delivery(
        orderId = "",
        shopId = "",
        customerId = "",
        address = "",
        phoneNumber = "",
        orderTime = LocalDateTime.now()
    )
}