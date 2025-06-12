FROM eclipse-temurin:21-jre

RUN mkdir -p /opt/app

COPY target/greeting-service-uebung-1.0-SNAPSHOT.jar  /opt/app/greeting-service.jar

WORKDIR /opt/app
CMD ["java","-jar","greeting-service.jar"]
