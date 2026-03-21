FROM maven:3.9.9-eclipse-temurin-11 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=builder /app/target/hh-flow-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
