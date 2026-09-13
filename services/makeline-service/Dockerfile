FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY services/makeline-service/pom.xml services/makeline-service/pom.xml
RUN mvn -q -pl services/makeline-service -am dependency:go-offline
COPY services/makeline-service/src services/makeline-service/src
RUN mvn -q -pl services/makeline-service -am package -DskipTests
FROM eclipse-temurin:17-jre
COPY --from=build /app/services/makeline-service/target/makeline-service-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
