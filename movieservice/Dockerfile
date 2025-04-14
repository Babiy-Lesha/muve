FROM openjdk:21-slim

WORKDIR /app

COPY target/movieservice-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8075

ENTRYPOINT ["java", "-jar", "app.jar"]