## Potato-Rider는 주문서버-판매자-배달대행사를 잇는 배달 중계 서비스입니다.

![image](https://github.com/user-attachments/assets/237ca827-d1cb-4f86-a76b-beda4ce07fbe)

## Architecture
![Pasted image 20240721145124](https://github.com/user-attachments/assets/9282470b-f7e6-43a5-b006-db0ed2903084)


## User Flow Diagram
![image](https://github.com/user-attachments/assets/93de8917-9f7d-43e7-90f4-c2a926ede7df)

## 주요 기술
- 언어 : Java 17
- 프레임워크 : Spring Boot, Spring Data JPA
- 빌드 도구 : Gradle
- API 문서화 : Swagger
- 배포 및 운영 :
  - 서버 : AWS EC2, Docker
  - CI/CD : GitHub Actions
  - 데이터베이스 : MongoDB
  - 캐시 : Redis (AWS Elastic Cache)
  - 메시징 시스템 : RabbitMQ
- 모니터링 : Prometheus + Grafana, CloudWatch, Brave, Zipkin
- 테스트 및 품질 : JaCoCo

## ERD
![Pasted image 20240704163024](https://github.com/user-attachments/assets/07b24cf6-89ca-41b5-84d6-fd05de272ded)

## Sequence Diagram
![Pasted image 20240717185654](https://github.com/user-attachments/assets/12c29ebf-6f4c-4728-b76e-d8f437492e2c)

---
## 프로젝트 관련 업로드

[TimeLine](https://tangpoo.tistory.com/188)

[주제 선정](https://tangpoo.tistory.com/185)

[설계 과정](https://tangpoo.tistory.com/187)

[Jacoco와 Test Coverage](https://tangpoo.tistory.com/190)

[Google Java Format 자동화 적용기](https://tangpoo.tistory.com/191)

[Zipkin과 OpenTracing](https://tangpoo.tistory.com/192)

성능 테스트(todo)





