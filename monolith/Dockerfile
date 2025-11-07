# Production-optimized Dockerfile for PnP Character Generator Service
# Base image: Amazon Corretto 24 (headless for smaller size)
FROM amazoncorretto:24-headless

# Metadata labels
LABEL maintainer="hipp"
LABEL org.opencontainers.image.title="PnP Character Generator Service"
LABEL org.opencontainers.image.description="Spring Boot microservice for pen-and-paper RPG character generation"
LABEL org.opencontainers.image.version="1.0-SNAPSHOT"

# Install curl for health checks and create a non-root user for security
RUN yum install -y --allowerasing shadow-utils curl && \
    groupadd -r appuser && \
    useradd -r -g appuser -m -d /home/appuser appuser && \
    yum clean all && \
    rm -rf /var/cache/yum

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY --chown=appuser:appuser build/libs/*-spring-boot.jar service.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Environment variables for Spring Boot (can be overridden at runtime)
ENV JAVA_OPTS="" \
    SERVER_PORT=8080

# Health check (checks if the application is responding)
# Adjust the endpoint if your app has a different health check path
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