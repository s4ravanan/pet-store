FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY services/product-service/pom.xml services/product-service/pom.xml
RUN mvn -q -pl services/product-service -am dependency:go-offline
COPY services/product-service/src services/product-service/src
RUN mvn -q -pl services/product-service -am package -DskipTests
FROM eclipse-temurin:17-jre
COPY --from=build /app/services/product-service/target/product-service-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
