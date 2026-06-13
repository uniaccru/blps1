FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY odoo-connector ./odoo-connector
COPY hh-flow ./hh-flow

RUN mvn -q -DskipTests -Pdocker package -pl hh-flow -am

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/hh-flow/target/hh-flow-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
