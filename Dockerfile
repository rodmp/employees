# Created 2026-04-13
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/employees-*.jar ./employees.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "employees.jar"]
