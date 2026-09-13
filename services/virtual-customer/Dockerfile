FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy root and all module POMs to satisfy reactor resolution
COPY pom.xml .
COPY config/ config/
COPY services/product-service/pom.xml services/product-service/pom.xml
COPY services/order-service/pom.xml services/order-service/pom.xml
COPY services/makeline-service/pom.xml services/makeline-service/pom.xml
COPY services/store-front/pom.xml services/store-front/pom.xml
COPY services/store-admin/pom.xml services/store-admin/pom.xml
COPY services/virtual-customer/pom.xml services/virtual-customer/pom.xml
COPY services/virtual-worker/pom.xml services/virtual-worker/pom.xml

RUN mvn -q -pl services/virtual-customer -am dependency:go-offline

COPY services/virtual-customer/src services/virtual-customer/src
RUN mvn -q -pl services/virtual-customer -am package -DskipTests -Dspotless.check.skip=true -Dcheckstyle.skip=true

FROM eclipse-temurin:21-jre
COPY --from=build /app/services/virtual-customer/target/virtual-customer-0.1.0-SNAPSHOT.jar /app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]
