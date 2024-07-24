FROM openjdk:17-alpine
LABEL authors="potatorider"

COPY ./build/libs/delivery-relay-service-0.0.1-SNAPSHOT.jar delivery-relay-service.jar

ENTRYPOINT ["java","-jar","/delivery-relay-service.jar"]