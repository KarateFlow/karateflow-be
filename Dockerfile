# Stage 1: Build the application
FROM maven:3-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies in a separate layer to cache them
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the JAR package skipping tests (tests run in CI/CD pipeline)
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
# Optimize JVM parameters for container execution (limiting heap within container limits)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
