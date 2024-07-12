package com.potatorider.exception;

public class DeliveryNotFoundException extends RuntimeException {

  public DeliveryNotFoundException() {
    super("요청한 배송정보를 찾을 수 없습니다.");
  }
}
