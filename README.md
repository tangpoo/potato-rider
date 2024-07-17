## Potato-Rider는 주문서버-판매자-배달대행사를 잇는 배달 중계 서비스입니다.

## Architecture
![Pasted image 20240717184741](https://github.com/user-attachments/assets/b2ed5ddd-bfad-41ef-8de9-f1605d125243)

## User Flow Snapshot
![image](https://github.com/user-attachments/assets/93de8917-9f7d-43e7-90f4-c2a926ede7df)

## 주요 기술
- 언어 : Java 17
- 프레임워크 : Spring Boot, Spring Data JPA
- 빌드 도구 : Gradle
- API 문서화 : Swagger
- 배포 및 운영 :
  - 서버 : AWS EC2, Docker
  - CI/CD : GitHub Actions
  - 데이터베이스 : MongoDB (AWS RDS)
  - 캐시 : Redis (AWS Elastic Cache)
  - 메시징 시스템 : RabbitMQ
- 모니터링 : Prometheus + Grafana, CloudWatch, Brave, Zipkin
- 테스트 및 품질 : JaCoCo

## ERD
![Pasted image 20240704144112](https://github.com/user-attachments/assets/85fda018-09fe-4261-96e0-af29e1cc262a)

## Feature
![Pasted image 20240717185654](https://github.com/user-attachments/assets/12c29ebf-6f4c-4728-b76e-d8f437492e2c)

---
## 프로젝트 관련 업로드

[TimeLine](https://tangpoo.tistory.com/188)

[주제 선정](https://tangpoo.tistory.com/185)

[설계 과정](https://tangpoo.tistory.com/187)

성능 테스트(todo)

Jacoco와 Test Coverage 이야기(todo)

Google Java Format 자동화 적용기(todo)

가치있는 테스트 식별하기(todo)





