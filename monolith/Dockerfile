# Multi-stage Dockerfile for PnP Character Generator Service (Monolith)
#
# IMPORTANT: This Dockerfile must be built from the PROJECT ROOT, not from the monolith folder.
# Build command: docker build -f monolith/Dockerfile -t pnp-monolith:latest .
# OR from monolith folder: docker build -f Dockerfile -t pnp-monolith:latest ..

# ============================================
# Stage 1: Builder (builds JAR from source)
# ============================================
FROM amazoncorretto:24 AS builder

# Install build tools required by Gradle Wrapper
RUN yum install -y findutils && yum clean all

WORKDIR /app

# Copy Gradle Wrapper (project root files)
COPY gradlew .
COPY gradle gradle/
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy all module build files (Gradle needs these to resolve the dependency graph)
COPY api/build.gradle.kts api/
COPY base/build.gradle.kts base/
COPY data/build.gradle.kts data/
COPY genefunk/build.gradle.kts genefunk/
COPY security/build.gradle.kts security/
COPY monolith/build.gradle.kts monolith/

# Copy source code for all modules (monolith depends on: api, base, data, genefunk, security)
COPY api/src api/src
COPY base/src base/src
COPY data/src data/src
COPY genefunk/src genefunk/src
COPY security/src security/src
COPY monolith/src monolith/src

# Build the monolith JAR (skip tests for Docker build speed)
RUN ./gradlew :monolith:bootJar -x test --no-daemon

# ============================================
# Stage 2: Runtime (production container)
# ============================================
FROM amazoncorretto:24-headless AS runtime

# Metadata
LABEL maintainer="hipp"
LABEL org.opencontainers.image.title="PnP Character Generator Service"
LABEL org.opencontainers.image.description="Spring Boot monolith for pen-and-paper RPG character generation"
LABEL org.opencontainers.image.version="1.0-SNAPSHOT"

# Install curl for health checks and create non-root user for security
RUN yum install -y --allowerasing shadow-utils curl && \
    groupadd -r appuser && \
    useradd -r -g appuser -m -d /home/appuser appuser && \
    yum clean all && \
    rm -rf /var/cache/yum

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder --chown=appuser:appuser /app/monolith/build/libs/*-spring-boot.jar service.jar

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Environment variables (override at runtime if needed)
ENV JAVA_OPTS="" \
    SERVER_PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Java 24 requires specific JVM arguments
ENTRYPOINT ["java", \
    "--enable-native-access=ALL-UNNAMED", \
    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-opens=java.base/java.io=ALL-UNNAMED", \
    "-jar", \
    "service.jar"]
