FROM openjdk:21-slim

WORKDIR /app

COPY target/eurekaServer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8774

ENTRYPOINT ["java", "-jar", "app.jar"]