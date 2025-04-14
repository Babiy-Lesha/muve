FROM openjdk:21-slim

WORKDIR /app

COPY target/userService-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8074

ENTRYPOINT ["java", "-jar", "app.jar"]