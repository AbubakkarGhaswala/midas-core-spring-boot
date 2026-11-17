# Stage 1: Build the application using the Maven Wrapper
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

# Copy Maven wrapper files and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Copy source files and application configuration
COPY src ./src
COPY application.yml ./application.yml

# Build the executable Spring Boot JAR skipping tests (tests contain intentional debugger loops)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built Spring Boot JAR and configuration from the builder stage
COPY --from=builder /workspace/target/midas-core-1.0.0.jar app.jar
COPY --from=builder /workspace/application.yml ./application.yml

# Default port configuration for Render and local runtime
ENV PORT=8080

# Expose default application port
EXPOSE 8080

# Launch application, dynamically respecting the PORT environment variable (e.g. on Render)
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${PORT:-${SERVER_PORT:-8080}} -jar app.jar"]
