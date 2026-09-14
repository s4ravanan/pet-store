FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY config/ config/
COPY services/admin-server/pom.xml services/admin-server/pom.xml
COPY services/product-service/pom.xml services/product-service/pom.xml
COPY services/order-service/pom.xml services/order-service/pom.xml
COPY services/makeline-service/pom.xml services/makeline-service/pom.xml
COPY services/store-front/pom.xml services/store-front/pom.xml
COPY services/store-admin/pom.xml services/store-admin/pom.xml
COPY services/virtual-customer/pom.xml services/virtual-customer/pom.xml
COPY services/virtual-worker/pom.xml services/virtual-worker/pom.xml
RUN mvn -q -pl services/store-front -am dependency:go-offline
COPY services/store-front/src services/store-front/src
RUN mvn -q -pl services/store-front -am package -DskipTests -Dspotless.check.skip=true -Dcheckstyle.skip=true
FROM eclipse-temurin:21-jre
COPY --from=build /app/services/store-front/target/store-front-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
