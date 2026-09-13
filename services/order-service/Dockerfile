FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY config/ config/
COPY services/product-service/pom.xml services/product-service/pom.xml
COPY services/order-service/pom.xml services/order-service/pom.xml
COPY services/makeline-service/pom.xml services/makeline-service/pom.xml
COPY services/store-front/pom.xml services/store-front/pom.xml
RUN mvn -q -pl services/order-service -am dependency:go-offline
COPY services/order-service/src services/order-service/src
RUN mvn -q -pl services/order-service -am package -DskipTests
FROM eclipse-temurin:21-jre
COPY --from=build /app/services/order-service/target/order-service-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
