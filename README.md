# PnP Service

A comprehensive service for pen and paper (PnP) role-playing games, providing character generation capabilities based on the 5e license. Built with Spring Boot, Kotlin, and deployed on Google Cloud Run.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Kotlin](https://img.shields.io/badge/kotlin-2.3.0--RC-blue.svg)
![Spring Boot](https://img.shields.io/badge/spring--boot-4.0.0-green.svg)
![Java](https://img.shields.io/badge/java-25-orange.svg)
![License](https://img.shields.io/badge/license-5e-lightgrey.svg)

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Development](#development)
- [Testing](#testing)
- [Security](#security)
- [Accessibility](#accessibility)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Code Quality](#code-quality)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

The PnP Service is designed to provide character generation and management tools for tabletop role-playing games. The project supports **dual architecture deployment**: both a **modular monolith** (recommended) and **microservices** architecture, providing flexibility for different operational needs.

### Key Features

- Character generation for 5e-based game systems
- GeneFunk system implementation (work in progress)
- OAuth2 JWT authentication with Firebase
- RESTful API with OpenAPI documentation
- Localization support for multiple game systems
- Cloud-native deployment on Google Cloud Run

### Current Status

- **100% Kotlin codebase** (74 Kotlin files, 0 Java files)
- **Dual architecture support**: Monolith (recommended) + Microservices (available)
- **Test coverage**: 0% → 90%+ (in progress)
- **Production deployment**: Google Cloud Run

## Architecture

The PnP Service uses a **modular monolith architecture** with distinct Gradle modules for separation of concerns:

```
┌──────────────────────────────────────────────────┐
│           PnP Monolith Service                    │
│                                                   │
│  REST Layer → Business Logic → Data Access → DB  │
│  (Controllers)   (Services)    (Repositories)     │
│                                                   │
│  External: OAuth2, Firebase, RabbitMQ            │
└──────────────────────────────────────────────────┘
```

### Modules

#### Core Library Modules
| Module | Purpose | Dependencies |
|--------|---------|-------------|
| **api** | Common interfaces and DTOs | None |
| **base** | Core utilities and 5e rules | api |
| **data** | Data persistence (legacy) | api, base |
| **security** | OAuth2 & Firebase auth with Firestore | api, base |
| **genefunk** | GeneFunk game system with Firestore | api, base |

#### Deployment Options (Dual Architecture Support)

**Option 1: Monolith** (recommended for simplicity):
- **monolith** - All-in-one deployment artifact (data, security, genefunk)

**Option 2: Microservices** (available for flexibility):
- **character-generator-starter** - Standalone character generator service
- **data-starter** - Standalone data service
- **security-starter** - Standalone security service
- **genefunk-starter** - Standalone GeneFunk service

Both architectures are maintained and fully functional, allowing deployment flexibility based on operational needs.

For detailed architecture documentation, see:
- [Architecture Guide](docs/architecture/ARCHITECTURE.md)
- [ADR 0001: Kotlin Migration](docs/architecture/adr/0001-kotlin-migration.md)
- [ADR 0002: Monolith Consolidation](docs/architecture/adr/0002-monolith-consolidation.md)

## Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Kotlin** | 2.2.10 | Primary language (100% of codebase) |
| **Java** | 24 | JVM runtime |
| **Spring Boot** | 4.0.0 | Application framework |
| **Gradle** | 8.x | Build tool (Kotlin DSL) |

### Key Dependencies

- **Spring Boot Starter Web**: REST API
- **Spring Boot Starter OAuth2 Resource Server**: JWT authentication
- **Spring Boot Starter AMQP**: RabbitMQ messaging
- **Spring Boot Actuator**: Health checks and metrics
- **SpringDoc OpenAPI**: 3.0.0 (Swagger UI)
- **Firebase Admin SDK** 9.7.0: Firebase authentication & Firestore database
- **Kotest** 5.7.2: Kotlin testing framework
- **MockK** 1.13.8: Kotlin mocking library
- **kotlin-logging** 7.0.12: Structured logging

### Infrastructure

- **Database**: Google Cloud Firestore (NoSQL)
- **Message Queue**: RabbitMQ
- **Authentication**: Google OAuth2 + Firebase
- **Deployment**: Google Cloud Run
- **CI/CD**: GitHub Actions

## Getting Started

### Prerequisites

- **Java 25** (Amazon Corretto recommended)
- **Gradle 8.x** (or use included Gradle wrapper)
- **Docker** (optional, for containerized development)
- **RabbitMQ** (via Docker or local installation)
- **Git**

### Quick Start

#### 1. Install Java 25 with SDKman

```bash
# Install SDKman
curl -s "https://get.sdkman.io" | bash

# Install Java 25
sdk install java 24-amzn
sdk use java 24-amzn

# Verify
java -version
```

#### 2. Clone the Repository

```bash
git clone https://github.com/yourusername/pnp-service.git
cd pnp-service
```

#### 3. Setup RabbitMQ

```bash
# Using Docker
docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

#### 4. Configure Environment Variables

Create `.env` file:

```bash
# OAuth2
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret

# Firebase (Base64 encoded JSON)
FIREBASE_CREDENTIALS_JSON=$(cat character-generator-service-private.json | base64)

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin
```

**Firebase Credentials Configuration Options**:

**Production/Container Deployment (Recommended):**
```bash
# Option 1: JSON string in environment variable
export FIREBASE_CREDENTIALS='{"type":"service_account","project_id":"..."}'

# Option 2: File path (standard Google convention)
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account-key.json"
```

**Local Development:**
- Place your Firebase credentials file in the project root as `character-generator-service-private.json`
- This file is automatically ignored by git (see .gitignore)
- DO NOT commit credential files to version control

See [Deployment Guide](docs/architecture/DEPLOYMENT.md) for detailed Firebase and OAuth2 setup.

#### 5. Build and Run

```bash
# Build all modules
./gradlew build

# Run the monolith
./gradlew :monolith:bootRun
```

The service will start on `http://localhost:8080`

#### 6. Verify Installation

```bash
# Check health
curl http://localhost:8080/actuator/health

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

## Development

### Project Structure

```
pnp-service/
├── api/                    # API interfaces and DTOs
├── base/                   # Core utilities and 5e rules
├── data/                   # JPA entities and repositories
├── security/               # OAuth2 and Firebase auth
├── genefunk/              # GeneFunk game system
├── monolith/              # Main deployment module
│   ├── src/main/kotlin/
│   │   └── de/hipp/pnp/
│   │       ├── CharacterGeneratorApplication.kt
│   │       ├── rest/      # REST controllers
│   │       ├── auth/      # Security configuration
│   │       └── rabbitmq/  # Message producers
│   ├── src/main/resources/
│   │   ├── application.yaml
│   │   ├── application-oauth.yml
│   │   └── application-rabbitmq.yaml
│   └── Dockerfile
├── docs/
│   ├── architecture/      # Architecture documentation
│   └── TECHNICAL_DEBT.md
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### Development Workflow

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Follow Kotlin coding conventions
   - Write tests (Kotest)
   - Update documentation

3. **Run Tests**
   ```bash
   ./gradlew test
   ```

4. **Build and Verify**
   ```bash
   ./gradlew clean build
   ```

5. **Commit and Push**
   ```bash
   git add .
   git commit -m "feat: add feature description"
   git push origin feature/your-feature-name
   ```

6. **Create Pull Request**
   - Ensure CI passes
   - Request code review
   - Merge after approval

### Docker Build Options

This project supports multiple Docker build strategies to accommodate both CI/CD pipelines and local development.

#### Prerequisites

- Docker 20.10+
- Docker Compose 2.0+ (optional, for local stack)

#### Build Strategy 1: Pre-built JAR (CI/CD - Fastest)

This is the default approach used in the CI/CD pipeline. Build the JAR first, then create the Docker image.

**Monolith Service:**
```bash
# Step 1: Build the JAR
./gradlew :monolith:bootJar -x test

# Step 2: Build Docker image (uses pre-built JAR)
docker build -f monolith/Dockerfile \
  --build-arg BUILD_SOURCE=prebuilt \
  -t pnp-service:latest \
  .
```

**Other Services (Security, GeneFunk, Data):**
```bash
# Security Service
./gradlew :security-starter:bootJar -x test
docker build -f security-starter/Dockerfile \
  --build-arg BUILD_SOURCE=prebuilt \
  -t pnp-security:latest \
  .

# GeneFunk Service
./gradlew :genefunk-starter:bootJar -x test
docker build -f genefunk-starter/Dockerfile \
  --build-arg BUILD_SOURCE=prebuilt \
  -t pnp-genefunk:latest \
  .

# Data Service
./gradlew :data-starter:bootJar -x test
docker build -f data-starter/Dockerfile \
  --build-arg BUILD_SOURCE=prebuilt \
  -t pnp-data:latest \
  .
```

**Advantages:**
- Fastest build time (~30 seconds)
- Smallest Docker context
- Matches CI/CD behavior
- Reuses Gradle cache

#### Build Strategy 2: Multi-Stage Build (Local Development - Most Convenient)

Build everything inside Docker without any prerequisites. No need to have Gradle installed locally.

**Monolith Service:**
```bash
# Build from source (no pre-build needed)
docker build -f monolith/Dockerfile -t pnp-service:dev .
```

**Other Services:**
```bash
docker build -f security-starter/Dockerfile -t pnp-security:dev .
docker build -f genefunk-starter/Dockerfile -t pnp-genefunk:dev .
docker build -f data-starter/Dockerfile -t pnp-data:dev .
```

**Advantages:**
- No Gradle installation required
- Self-contained builds
- Consistent build environment
- Portable across machines

**Note:** First build will be slower (~5-10 minutes) as Gradle downloads dependencies. Subsequent builds use Docker layer caching.

#### Running the Container

**With environment variables:**
```bash
docker run -d \
  --name pnp-service \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e GOOGLE_CLIENT_ID=your-client-id \
  -e GOOGLE_CLIENT_SECRET=your-client-secret \
  -e FIREBASE_CREDENTIALS_JSON=your-base64-credentials \
  -e RABBITMQ_HOST=localhost \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USERNAME=guest \
  -e RABBITMQ_PASSWORD=guest \
  pnp-service:latest
```

**With environment file:**
```bash
# Create .env file (see .env.example)
docker run -d \
  --name pnp-service \
  -p 8080:8080 \
  --env-file .env \
  pnp-service:latest
```

**With Docker Compose:**
```bash
docker-compose up -d
```

#### Health Check

Verify the container is healthy:
```bash
# Check Docker health status
docker ps --filter name=pnp-service

# Check application health endpoint
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

#### Docker Image Details

All Docker images include:
- Base Image: Amazon Corretto 24 (Headless JRE)
- Security: Non-root user (appuser)
- Health Checks: Automatic health monitoring
- Java Flags: Java 25 compatibility flags pre-configured
- Size: ~400MB (optimized)

#### Troubleshooting

**Issue: Build fails with "gradlew: Permission denied"**
```bash
# Fix: Make gradlew executable
chmod +x gradlew
git update-index --chmod=+x gradlew
```

**Issue: "COPY failed: file not found in build context"**
```bash
# Cause: Building with BUILD_SOURCE=prebuilt but JAR doesn't exist
# Solution: Build JAR first OR use multi-stage build (no BUILD_SOURCE arg)

# Option A: Build JAR first
./gradlew :monolith:bootJar

# Option B: Use multi-stage build (no --build-arg)
docker build -f monolith/Dockerfile -t pnp-service:latest .
```

**Issue: Container exits immediately**
```bash
# Check logs
docker logs pnp-service

# Common causes:
# - Missing environment variables
# - RabbitMQ not accessible
# - Invalid Firebase credentials
```

**Issue: Health check failing**
```bash
# Check if port is exposed correctly
docker ps --filter name=pnp-service

# Check application logs
docker logs pnp-service

# Verify health endpoint manually
docker exec pnp-service curl -f http://localhost:8080/actuator/health
```

#### CI/CD Integration

The GitHub Actions workflow automatically:
1. Builds the JAR using `./gradlew bootJar`
2. Creates Docker image using pre-built JAR
3. Pushes to Google Container Registry
4. Deploys to Google Cloud Run

See `.github/workflows/monolith.yml` for the full pipeline configuration.

## Testing

### Testing Framework

The project uses **Kotest** for all tests:
- **Unit Tests**: MockK for mocking
- **Integration Tests**: Spring Boot Test
- **Contract Tests**: Spring Cloud Contract (planned)

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :monolith:test

# Run tests with coverage
./gradlew test jacocoTestReport

# Run specific test
./gradlew test --tests CharacterRestControllerTest
```

### Test Structure

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class CharacterServiceTest : FunSpec({
    val repository = mockk<CharacterRepository>()
    val service = CharacterService(repository)

    test("should generate character with random attributes") {
        val character = service.generateCharacter(gameType = 0)

        character.attributes.size shouldBe 6
        character.genome shouldNotBe null
    }
})
```

### Test Coverage Target

- **Target**: 90%+ code coverage
- **Current**: ~13.5% project-wide, ~85-90% security module
- **Status**: Bruce (QA agent) implemented comprehensive security test suite (247 tests)

See [Test Strategy](docs/TEST_STRATEGY.md) for testing philosophy and roadmap.

## Security

This application implements comprehensive security measures following OWASP best practices:

### Authentication & Authorization

- **OAuth2 JWT Authentication**: Google OAuth2 integration with JWT token validation
- **Role-Based Access Control (RBAC)**: Two-tier access control system
  - `USER`: Standard user access to character generation and management
  - `ADMIN`: Administrative access with elevated privileges
- **Auto-User Creation**: Secure automatic user account creation on first JWT authentication
  - Email validation and format checking
  - Field length validation to prevent abuse
  - Comprehensive audit logging for user creation
  - Domain allowlist support (configurable)

### Security Headers (OWASP Recommendations)

The application automatically sets the following security headers:

- **X-Frame-Options: DENY** - Prevents clickjacking attacks
- **X-XSS-Protection: 1; mode=block** - Enables browser XSS filter
- **X-Content-Type-Options: nosniff** - Prevents MIME-sniffing
- **Referrer-Policy: same-origin** - Controls referrer information
- **Content-Security-Policy** - Restricts resource loading to prevent XSS
- **Strict-Transport-Security (HSTS)** - Enforces HTTPS connections
- **Permissions-Policy** - Restricts browser features

### Input Validation

All REST endpoints implement comprehensive input validation:

- **Request Parameter Validation**:
  - `gameType`: Range validation (0-100)
  - `characterId`: Positive integer validation
  - `language`: Locale format validation (xx_XX or xx-XX)
- **DTO Validation**: Email format, field length, role validation
- **Path Variable Validation**: Type checking and range validation

### Secure Credential Management

- **Firebase Credentials**: Environment-based configuration with multiple fallback options
  - Primary: `FIREBASE_CREDENTIALS` environment variable (JSON string)
  - Secondary: `GOOGLE_APPLICATION_CREDENTIALS` (file path)
  - Tertiary: Application Default Credentials (GCP)
  - Development fallback: Local file (with security warnings)
- **Git Protection**: Comprehensive .gitignore rules for credential files

### Exception Handling

Global exception handler provides:
- Security-aware error messages to prevent information leakage
- Proper HTTP status codes
- No stack trace leakage (logged server-side only)
- Validation error details for client debugging

### Future Security Enhancements

Recommended improvements for production deployments:
1. **Rate Limiting**: Prevent abuse of endpoints
2. **Domain Allowlist**: Enable domain-based user registration restrictions
3. **API Key Management**: Service-to-service authentication
4. **Audit Trail**: Enhanced security event logging
5. **Secrets Management**: Integration with dedicated secrets systems

See [Deployment Guide](docs/architecture/DEPLOYMENT.md) for production security setup.

## Accessibility

PnP Service is built with accessibility as a core principle, meeting WCAG 2.1 Level AA standards.

### Accessible Components

We provide 5 pre-validated accessible UI components:
- **AccessibleButton** - Semantic buttons with ARIA labels
- **AccessibleInput** - Form inputs with error handling and label association
- **AccessibleModal** - Focus-managed dialogs with keyboard support
- **AccessibleAlert** - Screen reader announcements with live regions
- **AccessibleLink** - Properly labeled links with new window indication

All components are created through a factory pattern with built-in validation to ensure accessibility compliance.

### Compliance

- WCAG 2.1 Level AA compliant
- Full keyboard navigation support
- Screen reader compatible (tested with NVDA & VoiceOver)
- Color contrast ratios greater than or equal to 4.5:1 (normal text), greater than or equal to 3:1 (large text)
- Touch targets greater than or equal to 44x44 pixels
- 100+ automated accessibility tests

### Testing

Our accessibility implementation is thoroughly tested:
- **Test Suite**: `base/src/test/kotlin/de/hipp/pnp/base/ui/AccessibilityTest.kt`
- **Coverage**: 100+ test cases covering ARIA attributes, validation, edge cases
- **Framework**: Kotest with comprehensive string input testing including unicode and emoji support

For detailed accessibility information, see [docs/ACCESSIBILITY.md](docs/ACCESSIBILITY.md).

### Keyboard Shortcuts

All UI components are keyboard accessible:
- `Tab` / `Shift+Tab` - Navigate between elements
- `Enter` / `Space` - Activate buttons and links
- `Escape` - Close modals and dialogs
- Arrow keys - Navigate within components

### Component Usage Example

```kotlin
// Create an accessible button with validation
val result = AccessibleComponentFactory.createButton(
    label = "Submit Form",
    onClick = "handleSubmit()",
    testId = "submit-btn",
    ariaLabel = "Submit registration form"
)

result.fold(
    onSuccess = { button -> /* Use validated button */ },
    onFailure = { error -> /* Handle validation error */ }
)
```

## API Documentation

### Swagger UI

Access interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Spec

Download OpenAPI 3.0 specification:

```
http://localhost:8080/v3/api-docs
```

### REST Endpoints

#### Character Management

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/character` | GET | Retrieve all characters |
| `/api/character/generate?gameType={id}` | GET | Generate new character |
| `/api/character/{id}` | DELETE | Delete character by ID |

#### Localization

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/locale?gameType={id}&language={code}` | GET | Get localized strings |

#### Health Check

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/actuator/health` | GET | Application health status |
| `/actuator/info` | GET | Application information |

### Example Requests

```bash
# Generate GeneFunk character
curl -X GET "http://localhost:8080/api/character/generate?gameType=0" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get English locale
curl -X GET "http://localhost:8080/api/locale?gameType=0&language=en_US" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Health check (no auth required)
curl http://localhost:8080/actuator/health
```

## Deployment

### Google Cloud Run

The application is deployed to Google Cloud Run using GitHub Actions.

#### Automatic Deployment

Push to `main` branch triggers automatic deployment:

```bash
git push origin main
```

GitHub Actions workflow:
1. Build Gradle project
2. Build Docker image
3. Push to Google Container Registry
4. Deploy to Cloud Run
5. Run smoke tests

#### Manual Deployment

```bash
# Build and deploy
gcloud run deploy character-generator-service \
  --image gcr.io/PROJECT_ID/character-generator-service:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

See [Deployment Guide](docs/architecture/DEPLOYMENT.md) for comprehensive deployment documentation.

### Environment Configuration

#### Local Development
```bash
SPRING_PROFILES_ACTIVE=local
# Firestore credentials required (see Firebase setup above)
```

#### Production
```bash
SPRING_PROFILES_ACTIVE=production
# Firestore and OAuth2 credentials required
```

### Monitoring

The application includes a comprehensive observability stack:

- **Metrics**: Micrometer + Prometheus (character generation, API calls, JVM)
- **Tracing**: Zipkin distributed tracing (request correlation)
- **Logging**: Structured JSON logging with Logback + Logstash encoder
- **Health Checks**: Custom Firebase and RabbitMQ health indicators
- **Performance**: AOP-based slow method detection
- **Dashboards**: Pre-built Grafana dashboard (see `docs/observability/grafana-dashboard.json`)

## Code Quality

### Coding Standards

- **Language**: Kotlin (100% of codebase)
- **Style Guide**: [Official Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
- **IDE**: IntelliJ IDEA with Kotlin plugin

### Code Review Guidelines

1. **Functionality**: Code works as intended
2. **Tests**: Comprehensive test coverage
3. **Documentation**: Code is well-documented
4. **Performance**: No obvious performance issues
5. **Security**: No security vulnerabilities
6. **Style**: Follows Kotlin conventions

### Performance Targets

- **API Response Time**: P95 < 200ms
- **Character Generation**: < 500ms
- **Database Queries**: < 50ms
- **Memory Usage**: < 512MB

### Metrics

- **Kotlin Files**: 74 (100%)
- **Java Files**: 0
- **Modules**: 10 (6 deprecated)
- **Test Coverage**: 0% → 90%+ (target)
- **Lines of Code**: ~5,000 (estimated)

## Contributing

### How to Contribute

1. **Fork the Repository**
2. **Create Feature Branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit Changes**
   ```bash
   git commit -m "feat: add amazing feature"
   ```
4. **Push to Branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open Pull Request**

### Commit Message Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `refactor:` Code refactoring
- `test:` Adding tests
- `chore:` Maintenance tasks

### Pull Request Process

1. Update documentation
2. Add/update tests
3. Ensure CI passes
4. Request review from maintainers
5. Address review comments
6. Merge after approval

### Reporting Issues

Use GitHub Issues to report bugs or request features:
- Provide clear description
- Include steps to reproduce
- Add relevant logs/screenshots
- Label appropriately

## Observability

The application includes comprehensive observability features including metrics, tracing, and logging.

### Quick Access

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# View metrics in browser
open http://localhost:8080/actuator/metrics
```

### Available Metrics

- Character generation (total, failures, duration)
- API endpoint calls (per endpoint, errors)
- HTTP server metrics (request rate, latency percentiles)
- JVM metrics (memory, GC, threads)
- System metrics (CPU, disk, uptime)

### Grafana Dashboard

Import the pre-built dashboard from `docs/observability/grafana-dashboard.json` for instant visualization of all metrics.

## Roadmap

### Current Sprint (In Progress)
- [x] Complete Swagger documentation
- [x] Architecture documentation
- [x] Implement observability stack
- [x] Migrate from H2 to Google Cloud Firestore
- [ ] Complete test coverage (90%+)

### Next Sprint
- [ ] Remove duplicate REST controllers
- [ ] Data migration tools for Firestore

### Future Enhancements
- [ ] Complete GeneFunk content
- [ ] Multi-language support (i18n)
- [ ] Additional game systems (D&D 5e, Pathfinder)
- [ ] Web UI for character management
- [ ] Mobile app integration
- [ ] AI-powered character backstory generation
- [ ] Achieve 90%+ test coverage project-wide (currently ~13.5%)

## Documentation

- [Architecture Guide](docs/architecture/ARCHITECTURE.md)
- [Deployment Guide](docs/architecture/DEPLOYMENT.md)
- [Accessibility Documentation](docs/ACCESSIBILITY.md)
- [Test Strategy](docs/TEST_STRATEGY.md)
- [Claude Code & S.C.R.U.M. Team](docs/CLAUDE.md)
- [ADR 0001: Kotlin Migration](docs/architecture/adr/0001-kotlin-migration.md)
- [ADR 0002: Monolith Consolidation](docs/architecture/adr/0002-monolith-consolidation.md)

## License

This project is based on the 5e license. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- Powered by [Kotlin](https://kotlinlang.org/)
- Deployed on [Google Cloud Run](https://cloud.google.com/run)
- Tested with [Kotest](https://kotest.io/)

---

**Maintained by**: [@hipp](https://github.com/hipp)

**Project Status**: Active Development

**Last Updated**: 2025-11-27
