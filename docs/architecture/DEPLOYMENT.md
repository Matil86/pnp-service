# Deployment Documentation

## Table of Contents
- [Local Development Setup](#local-development-setup)
- [Docker Build Process](#docker-build-process)
- [CI/CD Pipeline](#cicd-pipeline)
- [Cloud Run Deployment](#cloud-run-deployment)
- [Environment Variables](#environment-variables)
- [Health Checks](#health-checks)
- [Monitoring and Observability](#monitoring-and-observability)
- [Troubleshooting](#troubleshooting)

## Local Development Setup

### Prerequisites

Ensure you have the following installed:

- **Java 25 JDK** (Amazon Corretto 24 recommended)
- **Gradle 8.x** (or use included Gradle wrapper)
- **Docker** (for containerized development)
- **RabbitMQ** (via Docker or local installation)
- **Git**

### Installing Java 25 with SDKman

```bash
# Install SDKman if not already installed
curl -s "https://get.sdkman.io" | bash

# Install Amazon Corretto 24
sdk install java 24-amzn

# Verify installation
java -version
# Expected output: openjdk version "24" ... Amazon Corretto
```

The project includes a `.sdkmanrc` file for automatic Java version switching.

### Setting Up RabbitMQ

#### Option 1: Docker (Recommended)

```bash
# Run RabbitMQ with management console
docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=admin \
  rabbitmq:3-management

# Access management console: http://localhost:15672
# Username: admin, Password: admin
```

#### Option 2: Local Installation

```bash
# macOS
brew install rabbitmq
brew services start rabbitmq

# Ubuntu/Debian
sudo apt-get install rabbitmq-server
sudo systemctl start rabbitmq-server

# Verify RabbitMQ is running
sudo rabbitmqctl status
```

### Configuring Firebase Authentication

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project
   - Enable Authentication > Sign-in method > Google

2. **Generate Service Account**:
   - Project Settings > Service Accounts
   - Click "Generate new private key"
   - Save the JSON file as `character-generator-service-private.json`

3. **Configure OAuth2 Credentials**:
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - APIs & Services > Credentials
   - Create OAuth 2.0 Client ID (Web application)
   - Add authorized redirect URIs:
     - `http://localhost:8080/login/oauth2/code/google`
   - Note the Client ID and Client Secret

### Environment Configuration

Create a `.env` file in the project root:

```bash
# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret
OAUTH2_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google

# Firebase Configuration (Base64 encoded)
FIREBASE_CREDENTIALS_JSON=$(cat character-generator-service-private.json | base64)

# RabbitMQ Configuration
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin

# Application Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=local
```

Load environment variables:

```bash
# In your shell profile (~/.bashrc, ~/.zshrc, etc.)
export $(cat .env | xargs)

# Or use direnv (recommended)
brew install direnv
echo 'eval "$(direnv hook zsh)"' >> ~/.zshrc
direnv allow .
```

### Building the Project

```bash
# Clean and build all modules
./gradlew clean build

# Build without tests (faster)
./gradlew clean build -x test

# Build specific module
./gradlew :monolith:build
```

### Running Locally

#### Option 1: Gradle Spring Boot Plugin

```bash
# Run the monolith application
./gradlew :monolith:bootRun

# Run with specific profiles
./gradlew :monolith:bootRun --args='--spring.profiles.active=local,logging'

# Run with debug enabled (port 5005)
./gradlew :monolith:bootRun --debug-jvm
```

#### Option 2: Run JAR Directly

```bash
# Build the JAR
./gradlew :monolith:build

# Run the JAR
java --enable-native-access=ALL-UNNAMED \
     --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
     --add-opens=java.base/java.io=ALL-UNNAMED \
     -jar monolith/build/libs/monolith-1.0-SNAPSHOT.jar
```

#### Option 3: Docker Compose (Full Stack)

Create `docker-compose.yml` in project root:

```yaml
version: '3.8'

services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  pnp-service:
    build:
      context: .
      dockerfile: monolith/Dockerfile
    ports:
      - "8080:8080"
    environment:
      RABBITMQ_HOST: rabbitmq
      RABBITMQ_PORT: 5672
      RABBITMQ_USERNAME: admin
      RABBITMQ_PASSWORD: admin
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
      FIREBASE_CREDENTIALS_JSON: ${FIREBASE_CREDENTIALS_JSON}
    depends_on:
      rabbitmq:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 60s
```

Run with Docker Compose:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f pnp-service

# Stop all services
docker-compose down
```

### Verifying the Setup

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Expected output:
# {"status":"UP"}

# Test character generation endpoint (requires authentication)
curl -X GET "http://localhost:8080/api/character/generate?gameType=0" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Test locale endpoint
curl -X GET "http://localhost:8080/api/locale?gameType=0" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

## Docker Build Process

### Building the Docker Image

The project uses a multi-stage approach optimized for production.

#### Step 1: Build the Application JAR

```bash
# Build with Gradle
./gradlew :monolith:build -x test

# Verify JAR exists
ls -lh monolith/build/libs/*.jar
```

#### Step 2: Build Docker Image

```bash
# Build from project root
docker build -f monolith/Dockerfile -t pnp-service:latest .

# Build with specific tag
docker build -f monolith/Dockerfile -t pnp-service:1.0.0 .

# Build with build arguments
docker build -f monolith/Dockerfile \
  --build-arg JAVA_VERSION=24 \
  -t pnp-service:latest .
```

#### Step 3: Run Docker Container

```bash
# Run with environment variables
docker run -d \
  --name pnp-service \
  -p 8080:8080 \
  -e RABBITMQ_HOST=host.docker.internal \
  -e RABBITMQ_PORT=5672 \
  -e GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
  -e GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
  -e FIREBASE_CREDENTIALS_JSON=${FIREBASE_CREDENTIALS_JSON} \
  pnp-service:latest

# View logs
docker logs -f pnp-service

# Stop container
docker stop pnp-service
docker rm pnp-service
```

### Dockerfile Explained

```dockerfile
# Base image: Amazon Corretto 24 (headless for smaller size)
FROM amazoncorretto:24-headless

# Install dependencies and create non-root user
RUN yum install -y --allowerasing shadow-utils curl && \
    groupadd -r appuser && \
    useradd -r -g appuser -m -d /home/appuser appuser && \
    yum clean all && \
    rm -rf /var/cache/yum

# Copy JAR as non-root user
WORKDIR /app
COPY --chown=appuser:appuser target/*-spring-boot.jar service.jar

# Run as non-root
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM flags for Java 25 compatibility
ENTRYPOINT ["java", \
    "--enable-native-access=ALL-UNNAMED", \
    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-opens=java.base/java.io=ALL-UNNAMED", \
    "-jar", \
    "service.jar"]
```

**Key Features**:
- Non-root user (`appuser`) for security
- Health check using Spring Boot Actuator
- Java 25 compatibility flags
- Headless JRE for smaller image size
- Curl installed for health checks

### Optimizing Docker Image Size

```bash
# View image size
docker images pnp-service

# Analyze image layers
docker history pnp-service:latest

# Remove unused images
docker image prune -a
```

**Current Image Size**: ~450MB (Corretto 24 headless base)

## CI/CD Pipeline

### GitHub Actions Workflow

The project uses GitHub Actions for continuous integration and deployment.

**Workflow File**: `.github/workflows/monolith.yml`

```yaml
name: Build Character Generator Starter

concurrency:
  group: pnp-build-${{ github.ref }}
  cancel-in-progress: true

on:
  workflow_dispatch:  # Manual trigger
  push:
    paths:            # Auto-trigger on changes to these modules
      - build.gradle.kts
      - settings.gradle.kts
      - api/**
      - base/**
      - character-generator-starter/**
      - data/**
      - data-starter/**
      - monolith/**
      - genefunk/**
      - genefunk-starter/**
      - security/**
      - security-starter/**

jobs:
  build-and-deploy:
    uses: Matil86/github-actions/.github/workflows/pipeline-cloud-run-service.yaml@main
    with:
      build_image: character-generator-service
      service_name: character-generator-service
      pom_folder: monolith
      build_native: false
    secrets: inherit
```

### Pipeline Stages

The reusable workflow (`pipeline-cloud-run-service.yaml`) executes the following stages:

#### 1. Checkout Code
```bash
# Clone repository
git clone --depth=1 <repo>
```

#### 2. Setup Java
```bash
# Install Java 25 (Amazon Corretto)
actions/setup-java@v4
```

#### 3. Gradle Build
```bash
# Build project (includes tests)
./gradlew :monolith:build
```

#### 4. Docker Build
```bash
# Build and tag image
docker build -f monolith/Dockerfile \
  -t gcr.io/${GCP_PROJECT}/character-generator-service:${COMMIT_SHA} \
  -t gcr.io/${GCP_PROJECT}/character-generator-service:latest \
  .
```

#### 5. Push to Google Container Registry
```bash
# Authenticate with GCP
gcloud auth configure-docker

# Push image
docker push gcr.io/${GCP_PROJECT}/character-generator-service:${COMMIT_SHA}
docker push gcr.io/${GCP_PROJECT}/character-generator-service:latest
```

#### 6. Deploy to Cloud Run
```bash
# Deploy new revision
gcloud run deploy character-generator-service \
  --image gcr.io/${GCP_PROJECT}/character-generator-service:${COMMIT_SHA} \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars "KEY=VALUE"
```

#### 7. Smoke Tests
```bash
# Verify health endpoint
curl -f https://character-generator-service-xyz.run.app/actuator/health
```

### Required GitHub Secrets

Configure these in GitHub repository settings (Settings > Secrets and variables > Actions):

| Secret Name | Description |
|------------|-------------|
| `GCP_PROJECT_ID` | Google Cloud project ID |
| `GCP_SA_KEY` | Service account JSON key (Base64) |
| `GOOGLE_CLIENT_ID` | OAuth2 client ID |
| `GOOGLE_CLIENT_SECRET` | OAuth2 client secret |
| `FIREBASE_CREDENTIALS_JSON` | Firebase service account JSON (Base64) |
| `RABBITMQ_HOST` | RabbitMQ host URL |
| `RABBITMQ_USERNAME` | RabbitMQ username |
| `RABBITMQ_PASSWORD` | RabbitMQ password |

### Manual Workflow Trigger

```bash
# Trigger via GitHub CLI
gh workflow run monolith.yml

# Trigger via GitHub UI
# Actions > Build Character Generator Starter > Run workflow
```

### Pipeline Execution Time

- **Average Duration**: 5-8 minutes
- **Gradle Build**: 2-3 minutes
- **Docker Build**: 1-2 minutes
- **Push to GCR**: 1-2 minutes
- **Cloud Run Deploy**: 1-2 minutes

## Cloud Run Deployment

### Service Configuration

```yaml
Service Name: character-generator-service
Region: us-central1
Platform: Managed
Concurrency: 80 requests per container
CPU Allocation: CPU is always allocated
Min Instances: 0 (scale to zero)
Max Instances: 10
Timeout: 300 seconds (5 minutes)
Memory: 512 MiB
CPU: 1 vCPU
```

### Deployment Command

```bash
# Deploy manually via gcloud CLI
gcloud run deploy character-generator-service \
  --image gcr.io/${GCP_PROJECT}/character-generator-service:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --port 8080 \
  --memory 512Mi \
  --cpu 1 \
  --timeout 300 \
  --concurrency 80 \
  --min-instances 0 \
  --max-instances 10 \
  --set-env-vars "RABBITMQ_HOST=${RABBITMQ_HOST},GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}" \
  --set-secrets "FIREBASE_CREDENTIALS_JSON=firebase-creds:latest"
```

### Accessing the Deployed Service

```bash
# Get service URL
gcloud run services describe character-generator-service \
  --platform managed \
  --region us-central1 \
  --format 'value(status.url)'

# Example output:
# https://character-generator-service-abc123-uc.a.run.app

# Test health endpoint
curl https://character-generator-service-abc123-uc.a.run.app/actuator/health
```

### Updating Environment Variables

```bash
# Update single environment variable
gcloud run services update character-generator-service \
  --platform managed \
  --region us-central1 \
  --set-env-vars "NEW_VAR=value"

# Update multiple variables
gcloud run services update character-generator-service \
  --platform managed \
  --region us-central1 \
  --set-env-vars "VAR1=value1,VAR2=value2"

# Remove environment variable
gcloud run services update character-generator-service \
  --platform managed \
  --region us-central1 \
  --remove-env-vars "VAR_NAME"
```

### Scaling Configuration

```bash
# Set auto-scaling limits
gcloud run services update character-generator-service \
  --platform managed \
  --region us-central1 \
  --min-instances 1 \
  --max-instances 20

# Set concurrency (requests per container)
gcloud run services update character-generator-service \
  --platform managed \
  --region us-central1 \
  --concurrency 100
```

### Rollback to Previous Revision

```bash
# List revisions
gcloud run revisions list \
  --service character-generator-service \
  --platform managed \
  --region us-central1

# Rollback to specific revision
gcloud run services update-traffic character-generator-service \
  --platform managed \
  --region us-central1 \
  --to-revisions character-generator-service-00042-abc=100
```

## Environment Variables

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | `123456.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | `GOCSPX-abc123...` |
| `OAUTH2_REDIRECT_URI` | OAuth2 callback URL | `https://app.com/login/oauth2/code/google` |
| `FIREBASE_CREDENTIALS_JSON` | Firebase service account JSON (Base64) | `ewogICJ0eXBlIjogInNlcn...` |
| `RABBITMQ_HOST` | RabbitMQ server hostname | `rabbitmq.example.com` |
| `RABBITMQ_PORT` | RabbitMQ server port | `5672` |
| `RABBITMQ_USERNAME` | RabbitMQ username | `admin` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `secure-password` |

### Optional Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application HTTP port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profiles | `default` |
| `LOG_LEVEL` | Application log level | `INFO` |
| `JAVA_OPTS` | Additional JVM options | `""` |

### Loading Environment Variables from Google Secret Manager

```bash
# Create secret
echo -n "secret-value" | gcloud secrets create SECRET_NAME --data-file=-

# Grant Cloud Run service account access
gcloud secrets add-iam-policy-binding SECRET_NAME \
  --member serviceAccount:${SERVICE_ACCOUNT} \
  --role roles/secretmanager.secretAccessor

# Deploy with secret
gcloud run deploy character-generator-service \
  --set-secrets "ENV_VAR=SECRET_NAME:latest"
```

### Environment-Specific Configurations

#### Development
```bash
SPRING_PROFILES_ACTIVE=local,logging
RABBITMQ_HOST=localhost
```

#### Staging
```bash
SPRING_PROFILES_ACTIVE=staging
RABBITMQ_HOST=rabbitmq-staging.example.com
```

#### Production
```bash
SPRING_PROFILES_ACTIVE=production
RABBITMQ_HOST=rabbitmq-prod.example.com
MIN_INSTANCES=1
MAX_INSTANCES=20
```

## Health Checks

### Spring Boot Actuator Endpoints

The application exposes health check endpoints via Spring Boot Actuator.

#### Health Endpoint

```bash
# Simple health check
curl http://localhost:8080/actuator/health

# Response (healthy):
{
  "status": "UP"
}

# Response (unhealthy):
{
  "status": "DOWN",
  "components": {
    "diskSpace": {
      "status": "DOWN",
      "details": {
        "error": "No space left on device"
      }
    }
  }
}
```

#### Detailed Health Check

```bash
# Requires management.endpoint.health.show-details=always
curl http://localhost:8080/actuator/health

# Response:
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 123456789012,
        "threshold": 10485760
      }
    },
    "rabbit": {
      "status": "UP",
      "details": {
        "version": "3.12.0"
      }
    }
  }
}
```

#### Other Actuator Endpoints

```bash
# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Docker Health Check

The Dockerfile includes a built-in health check:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
```

Check container health:

```bash
# View health status
docker ps

# Inspect health check logs
docker inspect --format='{{json .State.Health}}' pnp-service | jq
```

### Cloud Run Health Checks

Cloud Run automatically performs health checks:

- **Startup Probe**: Waits up to 240 seconds for first successful health check
- **Liveness Probe**: Checks every 10 seconds; restarts container after 3 consecutive failures
- **Path**: `/` (HTTP GET request to port 8080)

Configure custom health check:

```bash
gcloud run services update character-generator-service \
  --platform managed \
  --region us-central1 \
  --health-checks-enabled \
  --health-check-path /actuator/health \
  --health-check-interval 30s \
  --health-check-timeout 3s
```

## Monitoring and Observability

### Logging

#### Application Logging

The application uses `kotlin-logging` for structured logging:

```kotlin
private val log = KotlinLogging.logger {}

log.info { "User authenticated: $userId" }
log.error(e) { "Failed to generate character" }
```

#### Log Levels

Configure via environment variable:

```bash
# Set root log level
export LOGGING_LEVEL_ROOT=INFO

# Set package-specific log level
export LOGGING_LEVEL_DE_HIPP_PNP=DEBUG
```

#### Viewing Logs

```bash
# Docker logs
docker logs -f pnp-service

# Cloud Run logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=character-generator-service" \
  --limit 50 \
  --format json

# Follow logs in real-time
gcloud logging tail "resource.type=cloud_run_revision AND resource.labels.service_name=character-generator-service"
```

### Metrics (Future Enhancement)

**Planned Integration**:
- Prometheus metrics via Micrometer
- Grafana dashboards
- Custom application metrics

### Tracing (Future Enhancement)

**Planned Integration**:
- Google Cloud Trace
- OpenTelemetry
- Distributed request tracing

## Troubleshooting

### Common Issues

#### 1. Application Fails to Start

**Symptom**: Container exits immediately after start

**Solution**:
```bash
# Check logs
docker logs pnp-service

# Common causes:
# - Missing environment variables
# - Invalid Firebase credentials
# - RabbitMQ connection failure

# Verify environment variables
docker exec pnp-service env | grep GOOGLE_CLIENT_ID
```

#### 2. RabbitMQ Connection Issues

**Symptom**: Logs show `Connection refused` or `ConnectException`

**Solution**:
```bash
# Verify RabbitMQ is running
docker ps | grep rabbitmq

# Test RabbitMQ connectivity
telnet localhost 5672

# Check RabbitMQ logs
docker logs rabbitmq

# Verify credentials
curl -u admin:admin http://localhost:15672/api/overview
```

#### 3. OAuth2 Authentication Errors

**Symptom**: 401 Unauthorized or JWT validation errors

**Solution**:
```bash
# Verify OAuth2 configuration
curl http://localhost:8080/.well-known/openid-configuration

# Check Firebase credentials
echo $FIREBASE_CREDENTIALS_JSON | base64 -d | jq

# Validate JWT token
# Use jwt.io to decode and inspect token
```

#### 4. Out of Memory Errors

**Symptom**: `java.lang.OutOfMemoryError: Java heap space`

**Solution**:
```bash
# Increase container memory (Cloud Run)
gcloud run services update character-generator-service \
  --memory 1Gi

# Set JVM heap size
export JAVA_OPTS="-Xmx512m -Xms256m"

# Monitor memory usage
docker stats pnp-service
```

#### 5. Slow Response Times

**Symptom**: Requests taking longer than expected

**Solution**:
```bash
# Check application metrics
curl http://localhost:8080/actuator/metrics/http.server.requests

# Increase concurrency (Cloud Run)
gcloud run services update character-generator-service \
  --concurrency 100
```

### Debug Mode

Enable debug logging:

```bash
# Local development
export LOGGING_LEVEL_DE_HIPP_PNP=DEBUG
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG

# Cloud Run
gcloud run services update character-generator-service \
  --set-env-vars "LOGGING_LEVEL_ROOT=DEBUG"
```

### Remote Debugging

```bash
# Run with debug port exposed
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
  -jar monolith/target/monolith-1.0-SNAPSHOT-spring-boot.jar

# Connect IntelliJ IDEA:
# Run > Edit Configurations > Add New Configuration > Remote JVM Debug
# Host: localhost, Port: 5005
```

### Useful Commands

```bash
# Check Java version
java -version

# Verify JAR contents
jar -tf monolith/target/monolith-1.0-SNAPSHOT-spring-boot.jar | head -20

# Test database connection (H2 console)
# Access: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb

# Force rebuild without cache
docker build --no-cache -f monolith/Dockerfile -t pnp-service:latest .

# Clean Docker resources
docker system prune -a --volumes
```

---

**Last Updated**: 2025-11-27

**See Also**:
- [Architecture Documentation](ARCHITECTURE.md)
- [Accessibility Documentation](../ACCESSIBILITY.md)
- [Test Strategy](../TEST_STRATEGY.md)
- [Claude Code & S.C.R.U.M. Team](../CLAUDE.md)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Google Cloud Run Documentation](https://cloud.google.com/run/docs)
