import {sleep, check, fail} from 'k6';
import http from 'k6/http';

export function setup() {
  if (!isRunnable()) {
    throw Error("Server is not runnable");
  }
}

export let options = {
  stages: [
    {duration: '1m', target: 10},
    {duration: '1m', target: 100},
    {duration: '1m', target: 500},
    {duration: '1m', target: 750},
    {duration: '1m', target: 1000}
  ],
  thresholds: {
    http_req_failed: ['rate<=0.05'],
    http_req_duration: ['p(90)<=1000', 'p(95)<=1250', 'p(100)<=1500'],
    checks: ['rate>=0.95']
  }
}

const param = {
  headers: {
    'Content-Type': 'application/json'
  }
}

const uri = "http://54.180.235.44:8080";
const URI = uri + "/api/v1/delivery";

export default () => {
  // 신규주문
  const req_saveDelivery = makeNewDelivery();

  const saveDelivery = http.post(URI, JSON.stringify(req_saveDelivery), param);
  check(saveDelivery, {
    'saveDelivery is OK 200': () => {
      if (saveDelivery.status !== 200) {
        console.info('saveDelivery result >>> ' + saveDelivery.body);
      }
      return saveDelivery.status === 200;
    }
  });

  sleep(1);

  const save_delivery = JSON.parse(saveDelivery.body);
  const deliveryId = save_delivery.id;

  // 주문접수
  const acceptDelivery = http.put(URI + `/${deliveryId}/accept`, param);
  check(acceptDelivery, {
    'acceptDelivery is OK 200': () => {
      if (acceptDelivery.status !== 200) {
        console.info('acceptDelivery result >>> ' + acceptDelivery.body);
      }
      return acceptDelivery.status === 200;
    }
  });
  sleep(1);

  // 라이더 배정
  const setDeliveryRider = http.put(URI + `/${deliveryId}/rider`, param);
  check(setDeliveryRider, {
    'setDeliveryRider is OK 200': () => {
      if (setDeliveryRider.status !== 200) {
        console.info('setDeliveryRider result >>> ' + setDeliveryRider.body);
      }
      return setDeliveryRider.status === 200;
    }
  });
  sleep(1);

  // 픽업완료
  const setPickedUp = http.put(URI + `/${deliveryId}/pickup`, param);
  check(setPickedUp, {
    'setPickedUp is OK 200': () => {
      if (setPickedUp.status !== 200) {
        console.info('setPickedUp result >>> ' + setPickedUp.body);
      }
      return setPickedUp.status === 200;
    }
  });
  sleep(1);

  // 배달완료
  const setComplete = http.put(URI + `/${deliveryId}/complete`, param);
  check(setComplete, {
    'setComplete is OK 200': () => {
      if (setComplete.status !== 200) {
        console.info('setComplete result >>> ' + setComplete.body);
      }
      return setComplete.status === 200;
    }
  });
  sleep(1);
}

function isRunnable() {
  const req_addDelivery = makeNewDelivery();
  req_addDelivery['riderId'] = null;

  let canRun = false;
  for (let i = 0; i < 3; i++) {
    const addDelivery = http.post(URI, JSON.stringify(req_addDelivery), param);
    if (addDelivery.status === 200) {
      canRun = true;
    }
    sleep(1);
  }
  return canRun;
}

function makeNewDelivery(time) {
  return {
    'orderId': 'orderId-' + time,
    'agencyId': 'agencyId-' + time,
    'shopId': 'shopId-' + time,
    'customerId': 'customerId-' + time,
    'address': 'address-' + time,
    'phoneNumber': 'phoneNumber-' + time,
    'comment': 'comment-' + time,
    'orderTime': new Date().toISOString(),
    'pickupTime': new Date().toISOString(),
    'finishTime': new Date().toISOString()
  }
}

function makeDelivery(id, status) {
  return {
    'id': id,
    'orderId': 'orderId-' + id,
    'riderId': null,
    'agencyId': 'agencyId-' + id,
    'shopId': 'shopId-' + id,
    'customerId': 'customerId-' + id,
    'address': 'address-' + id,
    'phoneNumber': 'phoneNumber-' + id,
    'comment': 'comment-' + id,
    'deliveryStatus': status,
    'orderTime': new Date().toISOString(),
    'pickupTime': new Date().toISOString(),
    'finishTime': new Date().toISOString()
  }
}