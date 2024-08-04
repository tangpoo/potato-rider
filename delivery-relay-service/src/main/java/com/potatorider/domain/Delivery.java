package com.potatorider.domain;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {

    @Id
    private String id;

    @NotNull(message = "주문번호는 필수값입니다.")
    private String orderId;

    private String riderId;

    private String agencyId;

    @NotNull(message = "상점 아이디는 필수값입니다.")
    private String shopId;

    @NotNull(message = "고객님의 아이디는 필수값입니다.")
    private String customerId;

    @NotNull(message = "배달주소는 필수 입력값입니다.")
    private String address;

    @NotNull(message = "전화번호는 필수 입력값입니다.")
    private String phoneNumber;

    private String comment;

    private DeliveryStatus deliveryStatus;

    @NotNull(message = "주문일시는 필수 입력값입니다.")
    private LocalDateTime orderTime;

    private LocalDateTime pickupTime;

    private LocalDateTime finishTime;
}
