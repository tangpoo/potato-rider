FROM openjdk:17-alpine
LABEL authors="potatorider"

COPY ./build/libs/rider-location-service-0.0.1-SNAPSHOT.jar rider-location-service.jar

ENTRYPOINT ["java","-jar","/rider-location-service.jar"]