# syntax=docker/dockerfile:1

# Dockerfile for PnP Character Generator Service (Monolith)
#
# DESIGN: Optimized for CI/CD where the JAR is pre-built.
# Uses BuildKit features for faster builds and better caching.
#
# GitHub Actions Workflow (build-cloud-run.yaml) does:
#   1. ./gradlew build -x test              # From project root - builds ALL modules
#   2. cd monolith                          # Change to monolith folder
#   3. docker build -f Dockerfile -t ... .  # Build with context = monolith folder
#
# The JAR is already in monolith/build/libs/ from step 1, so we just copy it.
#
# Usage for CI/CD:
#   ./gradlew :monolith:bootJar -x test    # From project root
#   cd monolith
#   docker build -t pnp-monolith:latest .  # Context = monolith folder
#
# Usage for local development from project root:
#   ./gradlew :monolith:bootJar -x test
#   docker build -f monolith/Dockerfile -t pnp-monolith:latest monolith/

FROM amazoncorretto:24-headless

# Install dependencies with cache mount for faster rebuilds
RUN --mount=type=cache,target=/var/cache/yum,sharing=locked \
    yum install -y --allowerasing shadow-utils curl && \
    groupadd -r appuser && \
    useradd -r -g appuser -m -d /home/appuser appuser && \
    yum clean all

WORKDIR /app

# Copy pre-built JAR
# Context is the monolith folder, so build/libs/ is relative to that
COPY --chown=appuser:appuser build/libs/*-spring-boot.jar service.jar

# Switch to non-root user for security
USER appuser

# Expose application port
EXPOSE 8080

# Environment variables (can be overridden at runtime)
ENV JAVA_OPTS="" \
    SERVER_PORT=8080

# Metadata (placed later to not invalidate cache on version changes)
LABEL maintainer="hipp" \
      org.opencontainers.image.title="PnP Character Generator Service" \
      org.opencontainers.image.description="Spring Boot monolith for pen-and-paper RPG character generation" \
      org.opencontainers.image.version="1.0-SNAPSHOT"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Java 24 requires specific JVM arguments for native access and module access
ENTRYPOINT ["java", \
    "--enable-native-access=ALL-UNNAMED", \
    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-opens=java.base/java.io=ALL-UNNAMED", \
    "-jar", \
    "service.jar"]
