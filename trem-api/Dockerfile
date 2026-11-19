# Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Execucao
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/trem-api.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
