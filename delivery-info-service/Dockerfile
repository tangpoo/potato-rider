FROM openjdk:17-alpine
LABEL authors="potatorider"

COPY ./build/libs/delivery-info-service-0.0.1-SNAPSHOT.jar delivery-info-service.jar

ENTRYPOINT ["java","-jar","/delivery-info-service.jar"]