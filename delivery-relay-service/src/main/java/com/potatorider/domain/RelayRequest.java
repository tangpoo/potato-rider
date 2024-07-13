package com.potatorider.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
public class RelayRequest {

    @Id private String id;

    private ReceiverType receiverType;

    private String receiverId;

    private Delivery delivery;

    public RelayRequest(
            final ReceiverType receiverType, final String receiverId, final Delivery delivery) {
        this.receiverType = receiverType;
        this.receiverId = receiverId;
        this.delivery = delivery;
    }
}
