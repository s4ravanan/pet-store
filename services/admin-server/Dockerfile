# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY services/admin-server/pom.xml services/admin-server/
COPY services/product-service/pom.xml services/product-service/
COPY services/order-service/pom.xml services/order-service/
COPY services/makeline-service/pom.xml services/makeline-service/
COPY services/store-front/pom.xml services/store-front/
COPY services/store-admin/pom.xml services/store-admin/
COPY services/virtual-customer/pom.xml services/virtual-customer/
COPY services/virtual-worker/pom.xml services/virtual-worker/

RUN mvn dependency:go-offline -B -pl services/admin-server -am || true

COPY services/admin-server/src services/admin-server/src
RUN mvn clean package -pl services/admin-server -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/services/admin-server/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
