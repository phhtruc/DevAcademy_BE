# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and configuration files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Preload dependencies to leverage caching
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN ./mvnw clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for better security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Set active Spring profile
ENV SPRING_PROFILES_ACTIVE=product

# Copy JAR file from build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose your application port
EXPOSE 8002

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
