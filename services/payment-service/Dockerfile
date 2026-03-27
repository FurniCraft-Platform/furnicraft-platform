FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew :services:payment-service:bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget curl

COPY --from=builder /app/services/payment-service/build/libs/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]