# Multi-stage Dockerfile for Data Access Service

# ============================================
# Stage 1: Builder (for building from source)
# ============================================
FROM gradle:8.5-jdk24 AS builder

WORKDIR /app

# Copy Gradle Wrapper
COPY gradlew .
COPY gradle gradle/
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy module build files
COPY api/build.gradle.kts api/
COPY base/build.gradle.kts base/
COPY data/build.gradle.kts data/
COPY data-starter/build.gradle.kts data-starter/

# Copy source code
COPY api/src api/src
COPY base/src base/src
COPY data/src data/src
COPY data-starter/src data-starter/src

# Build
RUN ./gradlew :data-starter:bootJar -x test --no-daemon

# Copy the built JAR to a known location
RUN cp data-starter/build/libs/*-spring-boot.jar /app/service.jar

# ============================================
# Stage 2: Pre-built JAR (for CI/CD)
# ============================================
FROM scratch AS prebuilt
COPY data-starter/build/libs/*-spring-boot.jar /app/service.jar

# ============================================
# Stage 3: Runtime
# ============================================
FROM amazoncorretto:24-headless AS runtime

# Metadata
LABEL maintainer="hipp"
LABEL org.opencontainers.image.title="PnP Data Access Service"
LABEL org.opencontainers.image.description="JPA data persistence service for PnP character generator"
LABEL org.opencontainers.image.version="1.0-SNAPSHOT"

# Install curl for health checks and create a non-root user for security
RUN yum install -y --allowerasing shadow-utils curl && \
    groupadd -r appuser && \
    useradd -r -g appuser -m -d /home/appuser appuser && \
    yum clean all && \
    rm -rf /var/cache/yum

# Set working directory
WORKDIR /app

# Copy JAR from builder or prebuilt stage
ARG BUILD_SOURCE=builder
COPY --from=${BUILD_SOURCE} --chown=appuser:appuser /app/service.jar service.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Environment variables for Spring Boot (can be overridden at runtime)
ENV JAVA_OPTS="" \
    SERVER_PORT=8080

# Health check (checks if the application is responding)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Java 24 requires specific JVM arguments to avoid sun.misc.Unsafe warnings
# These flags enable native access and open required modules
ENTRYPOINT ["java", \
    "--enable-native-access=ALL-UNNAMED", \
    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-opens=java.base/java.io=ALL-UNNAMED", \
    "-jar", \
    "service.jar"]
