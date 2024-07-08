package com.potatorider.exception;

public class DeliveryNotFountException extends RuntimeException{

    public DeliveryNotFountException() {
        super("요청한 배송정보를 찾을 수 없습니다.");
    }
}
