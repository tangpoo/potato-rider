package com.potatorider.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RelayRequest {

    @Id
    private String id;

    private ReceiverType receiverType;

    private String receiverId;

    private Delivery delivery;

    public RelayRequest(final ReceiverType receiverType, final String receiverId,
        final Delivery delivery) {
        this.receiverType = receiverType;
        this.receiverId = receiverId;
        this.delivery = delivery;
    }
}
