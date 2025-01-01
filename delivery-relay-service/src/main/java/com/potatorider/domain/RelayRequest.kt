package com.potatorider.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class RelayRequest(
    val receiverType: ReceiverType,
    val receiverId: String,
    val delivery: Delivery
) {
    @Id
    var id: String? = null
    var isAccepted = false
    var isEnabled = true
}
