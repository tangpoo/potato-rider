package com.potatorider.deliveryinfoservice.domain;

import com.potatorider.domain.Delivery;
import com.potatorider.domain.DeliveryStatus;
import java.time.LocalDateTime;

public class DeliverySteps {

    public static Delivery makeValidDeliveryWithDeliveryStatus(DeliveryStatus deliveryStatus) {
        String id = "id-1234";
        String orderId = "order1";
        String riderId = "rider-1234";
        String agencyId = "agency-1234";
        String shopId = "shop-1234";
        String customerId = "customer-1234";
        String address = "충주시 호반로...";
        String phoneNumber = "01011112222";
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(1);
        LocalDateTime pickupTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime finishTime = LocalDateTime.now().plusMinutes(30);

        return new Delivery(
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
            finishTime);
    }
}
