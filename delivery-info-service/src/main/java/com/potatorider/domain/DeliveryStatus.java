package com.potatorider.domain;

public enum DeliveryStatus {
  REQUEST("주문요청", "주문요청"),
  ACCEPT("주문승인", "주문승인"),
  RIDER_SET("배차완료", "배차완료"),
  PICKED_UP("픽업완료", "픽업완료"),
  COMPLETE("배송완료", "배송완료");

  private final String shopPerspective;
  private final String customerPerspective;

  DeliveryStatus(final String shopPerspective, final String customerPerspective) {
    this.shopPerspective = shopPerspective;
    this.customerPerspective = customerPerspective;
  }

  public DeliveryStatus getNext() {
    if (this.isLast()) {
      return COMPLETE;
    }
    return DeliveryStatus.values()[this.ordinal() + 1];
  }

  private boolean isLast() {
    return DeliveryStatus.values().length - 1 == this.ordinal();
  }
}
