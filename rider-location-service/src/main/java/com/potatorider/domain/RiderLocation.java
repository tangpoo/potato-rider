package com.potatorider.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiderLocation {

    private String id;
    private String deliveryId;
    private Float latitude;
    private Float longitude;
}
