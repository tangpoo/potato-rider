package com.potatorider.domain

enum class DeliveryStatus(
    private val shopPerspective: String,
    private val customerPerspective: String
) {
    REQUEST("주문요청", "주문요청"),
    ACCEPT("주문승인", "주문승인"),
    RIDER_SET("배차완료", "배차완료"),
    PICKED_UP("픽업완료", "픽업완료"),
    COMPLETE("배송완료", "배송완료");

    val next: DeliveryStatus
        get() {
            if (this.isLast) {
                return COMPLETE
            }
            return entries[ordinal + 1]
        }

    private val isLast: Boolean
        get() = entries.size - 1 == this.ordinal
}
