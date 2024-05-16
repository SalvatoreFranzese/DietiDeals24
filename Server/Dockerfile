FROM maven:3.8.4-openjdk-17-slim AS builder
COPY . /app
WORKDIR /app
RUN mvn clean package

FROM amazoncorretto:21
COPY --from=builder /app/target/Server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

