# PnP Service Architecture

## Table of Contents
- [System Overview](#system-overview)
- [Module Structure](#module-structure)
- [Technology Stack](#technology-stack)
- [Data Flow](#data-flow)
- [Security Architecture](#security-architecture)
- [Accessibility Architecture](#accessibility-architecture)
- [Deployment Architecture](#deployment-architecture)
- [Module Dependencies](#module-dependencies)

## System Overview

The PnP Service is a comprehensive platform for pen and paper (PnP) role-playing game character generation and management. The system has undergone a significant architectural evolution from a distributed microservices architecture to a consolidated monolith approach.

### Architecture Evolution

**Previous Architecture: Microservices**
- Character Generator Service (separate)
- Data Service (separate)
- Security Service (separate)
- GeneFunk Service (separate)

**Current Architecture: Monolith Consolidation**
- All services consolidated into a single monolith deployment
- Modular Gradle structure maintained for code organization
- Simplified deployment and operational overhead
- Improved performance through elimination of network calls

### Design Principles

1. **Modularity**: Despite monolith deployment, code is organized in separate Gradle modules
2. **5e License Compliance**: Built to support 5e-based game systems
3. **Event-Driven**: RabbitMQ messaging for decoupled communication
4. **Security-First**: OAuth2 JWT-based authentication throughout
5. **Cloud-Native**: Designed for Google Cloud Run deployment

## Module Structure

The project consists of 10 Gradle modules organized into distinct layers:

### Core Modules

#### 1. api
**Purpose**: Common API definitions and interfaces
**Key Components**:
- `FiveECharacterService`: Character service interface
- `FiveECharacterProducer`: Character producer interface
- `FiveEDataProducer`: Data producer interface
- `BaseCharacter`: Abstract character model
- `BaseCharacterClass`: Abstract character class model
- `Species`: Abstract species model

**Dependencies**: None (foundation module)

#### 2. base
**Purpose**: Core functionality and shared components
**Key Components**:
- `DiceRoller`: 5e dice rolling implementation
- `Attribute5e`: 5e attribute system
- `Feature5e`: 5e feature system
- `BaseProducer`: RabbitMQ producer base class
- Converters for JPA persistence
- Common constants and DTOs

**Dependencies**: api

#### 3. data
**Purpose**: Data persistence and management
**Key Components**:
- JPA repositories
- Entity models
- H2 in-memory database configuration
- Localization properties and management

**Dependencies**: api, base

**Technology**: Spring Data JPA, H2 Database

#### 4. security
**Purpose**: Authentication and authorization
**Key Components**:
- `FirebaseConfiguration`: Firebase admin SDK setup
- `User`: User entity model
- `UserService`: User management
- `UserRepository`: User data access
- `UserListener`: RabbitMQ user event listener
- `Role`: Role enumeration

**Dependencies**: api, base

**Technology**: Spring Security, Firebase Admin SDK, OAuth2

#### 5. genefunk
**Purpose**: GeneFunk game system implementation
**Key Components**:
- `GeneFunkCharacterService`: Character generation logic
- `GeneFunkCharacter`: Character entity
- `GeneFunkGenomeService`: Genome management
- `GeneFunkClassService`: Class management
- Character, Class, Genome, Feature repositories

**Dependencies**: api, base, data

**Status**: Work in progress

### Starter Modules (Legacy)

#### 6. character-generator-starter
**Purpose**: Original standalone character generator service
**Status**: DEPRECATED - Functionality moved to monolith
**Contains**: REST controllers, RabbitMQ producers, security config

#### 7. data-starter
**Purpose**: Standalone data service
**Status**: DEPRECATED - Functionality moved to monolith
**Contains**: Data service application

#### 8. security-starter
**Purpose**: Standalone security service
**Status**: DEPRECATED - Functionality moved to monolith
**Contains**: Security service application

#### 9. genefunk-starter
**Purpose**: Standalone GeneFunk service
**Status**: DEPRECATED - Functionality moved to monolith
**Contains**: GeneFunk service application

### Deployment Module

#### 10. monolith
**Purpose**: Consolidated application deployment
**Key Components**:
- `CharacterGeneratorApplication`: Main Spring Boot application
- `CharacterRestController`: Character management REST API
- `LocaleRestController`: Localization REST API
- `SecurityConfiguration`: OAuth2 security setup
- `CharacterProducer`: Character operations via RabbitMQ
- `LocaleProducer`: Locale operations via RabbitMQ
- `RequestLoggingFilter`: HTTP request logging

**Dependencies**: data, genefunk, security (transitively includes all modules)

**Configuration Files**:
- `application.yaml`: Main configuration
- `application-oauth.yml`: OAuth2 settings
- `application-rabbitmq.yaml`: RabbitMQ configuration
- `application-h2-database.properties`: H2 database settings
- `application-logging.yml`: Logging configuration
- `application-genefunk-crb.yaml`: GeneFunk core rulebook data
- `application-genefunk-crb-locale.yaml`: GeneFunk localization

## Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 24 | Primary runtime |
| **Kotlin** | 2.2.10 | Primary language (100% of codebase) |
| **Spring Boot** | 3.5.5 | Application framework |
| **Gradle** | 8.x | Build tool (Kotlin DSL) |

### Spring Framework

| Component | Purpose |
|-----------|---------|
| Spring Boot Starter Web | REST API development |
| Spring Boot Starter AMQP | RabbitMQ integration |
| Spring Boot Starter Data JPA | Database persistence |
| Spring Boot Starter OAuth2 Resource Server | OAuth2 authentication |
| Spring Boot Starter Actuator | Health checks and metrics |
| Spring Boot Configuration Processor | Configuration metadata |
| Spring WebFlux | Reactive web client |

### Messaging & Events

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Spring AMQP** | (Spring Boot managed) | RabbitMQ abstraction |
| **RabbitMQ** | External | Message queue for async operations |

### Data & Persistence

| Technology | Version | Purpose |
|-----------|---------|---------|
| **H2 Database** | (Spring Boot managed) | In-memory development database |
| **Spring Data JPA** | (Spring Boot managed) | Data access layer |
| **Jackson** | (Spring Boot managed) | JSON serialization |
| **Jackson JSR310** | (Spring Boot managed) | Java 8 date/time support |

### Security & Authentication

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Spring Security** | (Spring Boot managed) | Security framework |
| **OAuth2 Resource Server** | (Spring Boot managed) | JWT token validation |
| **Firebase Admin SDK** | 9.5.0 | Firebase authentication integration |

### Logging & Observability

| Technology | Version | Purpose |
|-----------|---------|---------|
| **kotlin-logging** | 7.0.12 | Kotlin-idiomatic logging |
| **Spring Boot Actuator** | (Spring Boot managed) | Health checks, metrics |

### Documentation

| Technology | Version | Purpose |
|-----------|---------|---------|
| **SpringDoc OpenAPI** | 2.8.11 | Swagger/OpenAPI documentation |

### Testing

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Kotest** | 5.7.2 | Kotlin testing framework |
| **MockK** | 1.13.8 | Kotlin mocking library |
| **Spring Boot Test** | (Spring Boot managed) | Spring integration testing |

### Build & Deployment

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Gradle Test** | (Gradle managed) | Test execution |
| **Kotlin Gradle Plugin** | 2.2.10 | Kotlin compilation |
| **Spring Boot Gradle Plugin** | (Spring Boot managed) | Executable JAR packaging |
| **Docker** | - | Containerization |
| **Amazon Corretto** | 24 (headless) | Base Docker image |

### Cloud & Infrastructure

| Technology | Purpose |
|-----------|---------|
| **Google Cloud Run** | Serverless container deployment |
| **GitHub Actions** | CI/CD pipeline |

## Data Flow

### Character Generation Flow

```
1. Client Request
   └─> POST /api/character/generate?gameType=0
       └─> CharacterRestController.generateCharacter()
           └─> CharacterProducer.generate(gameType)
               └─> RabbitMQ Queue: character.generate
                   └─> GeneFunkCharacterService (Listener)
                       ├─> GeneFunkGenomeService.randomGenome()
                       ├─> GeneFunkClassService.randomClass()
                       ├─> DiceRoller.rollAttributes()
                       └─> GeneFunkCharacterRepository.save()
                           └─> Response: Character JSON
```

### Localization Flow

```
1. Client Request
   └─> GET /api/locale?gameType=0
       └─> LocaleRestController.getLocale()
           └─> LocaleProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US")
               └─> RabbitMQ Queue: locale.request
                   └─> Data Service (Listener)
                       └─> LocalizationRepository.findByGameTypeAndLanguage()
                           └─> Response: Locale JSON
```

### User Authentication Flow

```
1. Client Authentication
   └─> OAuth2 Authorization Code Flow
       ├─> Google OAuth2 Provider
       │   └─> Authorization Code
       │       └─> Token Exchange
       │           └─> JWT Access Token
       └─> JWT Token Validation
           ├─> Spring Security OAuth2 Resource Server
           │   └─> Firebase Admin SDK (Token Verification)
           └─> SecurityContext Population
               └─> Authorized Request Processing
```

### Message Queue Architecture

**RabbitMQ Queues**:
- `character.generate`: Character generation requests
- `character.delete`: Character deletion requests
- `character.list`: Character list requests
- `locale.request`: Localization data requests
- `user.events`: User lifecycle events

**Message Format** (`DefaultMessage`):
```kotlin
{
  "header": {
    "userId": "string",
    "timestamp": "ISO-8601",
    "correlationId": "uuid"
  },
  "payload": "JSON string"
}
```

## Security Architecture

### OAuth2 JWT Flow

```
┌─────────┐          ┌──────────┐          ┌─────────────┐          ┌──────────┐
│ Client  │          │  Google  │          │   PnP       │          │ Firebase │
│         │          │  OAuth2  │          │  Service    │          │          │
└────┬────┘          └────┬─────┘          └──────┬──────┘          └────┬─────┘
     │                    │                       │                      │
     │ 1. Redirect to     │                       │                      │
     │    OAuth2          │                       │                      │
     ├───────────────────>│                       │                      │
     │                    │                       │                      │
     │ 2. User Login      │                       │                      │
     │    & Consent       │                       │                      │
     ├───────────────────>│                       │                      │
     │                    │                       │                      │
     │ 3. Authorization   │                       │                      │
     │    Code            │                       │                      │
     │<───────────────────┤                       │                      │
     │                    │                       │                      │
     │ 4. Exchange Code   │                       │                      │
     │    for Token       │                       │                      │
     ├───────────────────>│                       │                      │
     │                    │                       │                      │
     │ 5. JWT Token       │                       │                      │
     │<───────────────────┤                       │                      │
     │                    │                       │                      │
     │ 6. API Request     │                       │                      │
     │    with JWT        │                       │                      │
     ├────────────────────┴──────────────────────>│                      │
     │                                             │                      │
     │                                             │ 7. Validate JWT     │
     │                                             │    with Firebase    │
     │                                             ├─────────────────────>│
     │                                             │                      │
     │                                             │ 8. Token Valid      │
     │                                             │<─────────────────────┤
     │                                             │                      │
     │ 9. API Response                             │                      │
     │<────────────────────────────────────────────┤                      │
     │                                             │                      │
```

### Security Components

#### 1. OAuth2 Resource Server Configuration
- JWT token validation using Firebase Admin SDK
- Bearer token extraction from Authorization header
- Stateless authentication (no sessions)

#### 2. Security Filter Chain
- `RequestLoggingFilter`: Logs all HTTP requests with user context
- OAuth2 Resource Server filters
- CORS configuration for cross-origin requests

#### 3. Protected Endpoints
```
/api/character/**  - Requires authentication
/api/locale/**     - Requires authentication
/actuator/health   - Public (for load balancer health checks)
/swagger-ui/**     - Public (API documentation)
/v3/api-docs/**    - Public (OpenAPI spec)
```

#### 4. User Management
- Users stored in H2 database (User entity)
- User events published to RabbitMQ
- Firebase UID as primary identifier
- Role-based access control (ROLE_USER, ROLE_ADMIN)

### Environment Variables (Secrets)

```bash
# OAuth2
GOOGLE_CLIENT_ID=<Google OAuth client ID>
GOOGLE_CLIENT_SECRET=<Google OAuth client secret>
OAUTH2_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google

# Firebase
FIREBASE_CREDENTIALS_JSON=<Base64 encoded service account JSON>

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

## Accessibility Architecture

PnP Service implements WCAG 2.1 Level AA compliant accessible UI components as part of the base module. This ensures all user interfaces built with the service meet international accessibility standards.

### Accessible Components (`base/ui`)

Pre-validated UI components ensuring WCAG 2.1 Level AA compliance:

```
AccessibleComponentFactory
├── createButton()      → AccessibleButton
├── createInput()       → AccessibleInput
├── createModal()       → AccessibleModal
├── createAlert()       → AccessibleAlert (partial implementation)
└── createLink()        → AccessibleLink (not in factory yet)
```

**Design Pattern**: Factory with validation
- All components validated at creation time
- Returns `Result<T>` for safe error handling
- Built-in ARIA attribute management
- Semantic HTML generation

**Component Architecture**:

```kotlin
// Factory creates validated components
AccessibleComponentFactory.createButton(
    label: String,
    onClick: String,
    testId: String,
    ariaLabel: String = label,
    type: ButtonType = BUTTON,
    disabled: Boolean = false
): Result<AccessibleButton>

// Component provides HTML generation
button.toHtml(): String

// Component validates accessibility requirements
button.validate(): List<String>
```

### WCAG 2.1 Level AA Compliance

The accessible components meet the following WCAG criteria:

**Perceivable**:
- 1.1.1 Non-text Content: All components have text alternatives via aria-label
- 1.3.1 Info and Relationships: Semantic HTML and ARIA relationships
- 1.3.2 Meaningful Sequence: Logical content order maintained
- 1.4.3 Contrast (Minimum): 4.5:1 for normal text, 3:1 for large text (documented)

**Operable**:
- 2.1.1 Keyboard: All functionality available via keyboard
- 2.1.2 No Keyboard Trap: Focus can always move away
- 2.4.3 Focus Order: Logical and consistent tab order
- 2.4.7 Focus Visible: Clearly visible focus indicators (documented)

**Understandable**:
- 3.2.4 Consistent Identification: Components behave consistently
- 3.3.1 Error Identification: Errors described in text
- 3.3.2 Labels or Instructions: All inputs have labels
- 3.3.3 Error Suggestion: Error messages provide correction guidance

**Robust**:
- 4.1.2 Name, Role, Value: Proper ARIA implementation
- 4.1.3 Status Messages: Live regions for dynamic content

### Testing Architecture

**Automated Testing**: 100+ tests in `AccessibilityTest.kt` covering:
- ARIA attribute verification (role, aria-label, aria-describedby, etc.)
- Label association validation (for/id matching)
- String edge cases (empty, whitespace, unicode, emoji)
- Factory validation (success/failure paths)
- Component state management (disabled, invalid, required)
- Error handling and messaging

**Test Framework**: Kotest with Result type validation

**Test Coverage**:
```
AccessibilityTest.kt (450 lines)
├── Button Tests (6 scenarios)
├── Input Tests (5 scenarios)
├── Modal Tests (4 scenarios)
├── Alert Tests (4 scenarios)
├── Link Tests (4 scenarios)
├── Factory Tests (2 scenarios)
└── Edge Cases (4 scenarios)
Total: 29+ test scenarios, 100+ assertions
```

### Component Module Structure

```
base/
├── src/main/kotlin/de/hipp/pnp/base/ui/
│   └── AccessibleComponents.kt (419 lines)
│       ├── AccessibleButton (data class + validation)
│       ├── AccessibleInput (data class + validation)
│       ├── AccessibleModal (data class + validation)
│       ├── AccessibleAlert (data class + validation)
│       ├── AccessibleLink (data class + validation)
│       └── AccessibleComponentFactory (object)
└── src/test/kotlin/de/hipp/pnp/base/ui/
    └── AccessibilityTest.kt (450 lines)
```

### HTML Generation Pattern

Each component generates semantic HTML with proper ARIA attributes:

```kotlin
// Example: AccessibleButton
fun toHtml(): String = """
    <button
        type="${type.name.lowercase()}"
        aria-label="$ariaLabel"
        ${if (disabled) "disabled" else ""}
        ${if (ariaDescribedBy != null) "aria-describedby=\"$ariaDescribedBy\"" else ""}
        data-testid="$testId"
        onclick="$onClick"
        class="accessible-button">
        $label
    </button>
""".trimIndent()
```

### Validation Architecture

**Two-Level Validation**:

1. **Component-Level**: Each component has a `validate()` method
   ```kotlin
   fun validate(): List<String> {
       val issues = mutableListOf<String>()
       if (label.isBlank()) {
           issues.add("Button label cannot be blank")
       }
       return issues
   }
   ```

2. **Factory-Level**: Factory methods wrap validation in Result type
   ```kotlin
   fun createButton(...): Result<AccessibleButton> {
       val button = AccessibleButton(...)
       val issues = button.validate()
       return if (issues.isEmpty()) {
           Result.success(button)
       } else {
           Result.failure(IllegalArgumentException("Validation failed: ..."))
       }
   }
   ```

### Integration Points

**Usage in Applications**:
- REST controllers can use components to generate accessible HTML responses
- Template engines can integrate component HTML
- API responses can include accessible markup
- Client applications receive WCAG-compliant HTML

**Example Integration**:
```kotlin
@RestController
class CharacterController {
    @GetMapping("/character/{id}")
    fun getCharacter(@PathVariable id: Long): String {
        val character = characterService.findById(id)

        val deleteButton = AccessibleComponentFactory.createButton(
            label = "Delete Character",
            onClick = "deleteCharacter($id)",
            testId = "delete-btn-$id",
            ariaLabel = "Delete character ${character.name}"
        ).getOrThrow()

        return deleteButton.toHtml()
    }
}
```

### Future Enhancements

Planned accessibility architecture improvements:
- [ ] CSS templates with accessible default styling
- [ ] JavaScript focus management utilities
- [ ] Touch target size validation
- [ ] Color contrast testing utilities
- [ ] Additional components (tabs, accordions, breadcrumbs)
- [ ] Internationalization (i18n) support for ARIA labels
- [ ] Automated accessibility auditing in CI/CD

See [Accessibility Documentation](../ACCESSIBILITY.md) for complete user-facing accessibility information.

## Deployment Architecture

### Google Cloud Run Deployment

```
┌─────────────────────────────────────────────────────────┐
│                    Google Cloud Run                     │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │    character-generator-service (Container)         │ │
│  │                                                     │ │
│  │  ┌────────────────────────────────────────────┐   │ │
│  │  │  Spring Boot Application (Port 8080)       │   │ │
│  │  │                                             │   │ │
│  │  │  - Character REST API                       │   │ │
│  │  │  - Locale REST API                          │   │ │
│  │  │  - OAuth2 Security                          │   │ │
│  │  │  - RabbitMQ Producers                       │   │ │
│  │  │  - Actuator Health Checks                   │   │ │
│  │  └────────────────────────────────────────────┘   │ │
│  │                                                     │ │
│  │  Base Image: amazoncorretto:24-headless            │ │
│  │  User: appuser (non-root)                          │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  Auto-scaling: 0-10 instances                           │
│  CPU: 1 vCPU                                            │
│  Memory: 512Mi                                          │
│  Concurrency: 80 requests/instance                      │
└─────────────────────────────────────────────────────────┘
                        │
                        │ HTTPS
                        ▼
              ┌─────────────────┐
              │  Load Balancer  │
              └─────────────────┘
                        │
                        ▼
                 ┌─────────────┐
                 │   Clients   │
                 └─────────────┘

External Services:
┌─────────────────┐   ┌──────────────┐   ┌─────────────┐
│  Google OAuth2  │   │   Firebase   │   │  RabbitMQ   │
└─────────────────┘   └──────────────┘   └─────────────┘
```

### Container Architecture

**Dockerfile Highlights**:
- Base: Amazon Corretto 24 (headless)
- Non-root user: `appuser`
- Health check: `/actuator/health` (30s interval)
- JVM flags for Java 24 compatibility:
  - `--enable-native-access=ALL-UNNAMED`
  - `--add-opens=java.base/sun.nio.ch=ALL-UNNAMED`
  - `--add-opens=java.base/java.io=ALL-UNNAMED`

### CI/CD Pipeline

**GitHub Actions Workflow** (`.github/workflows/monolith.yml`):

```yaml
Trigger: Push to main (on module changes)
Steps:
  1. Build Gradle project (./gradlew build)
  2. Build Docker image
  3. Push to Google Container Registry
  4. Deploy to Google Cloud Run
  5. Run smoke tests
```

### High Availability Features

1. **Auto-scaling**: 0-10 instances based on request load
2. **Health Checks**: Actuator health endpoint monitored every 30s
3. **Graceful Shutdown**: Spring Boot graceful shutdown enabled
4. **Zero-downtime Deployments**: Cloud Run rolling updates
5. **Request Timeout**: 300s (5 minutes) max request duration

## Module Dependencies

### Dependency Graph

```
                           ┌─────┐
                           │ api │ (Foundation)
                           └──┬──┘
                              │
                   ┌──────────┼──────────┐
                   │          │          │
                ┌──▼───┐   ┌──▼──────┐  │
                │ base │   │ security│  │
                └──┬───┘   └──┬──────┘  │
                   │          │         │
                   │       ┌──▼─┐       │
                   └──────>│data│<──────┘
                           └──┬─┘
                              │
                          ┌───▼────┐
                          │genefunk│
                          └───┬────┘
                              │
                        ┌─────▼─────┐
                        │  monolith │ (Deployment)
                        └───────────┘

Legacy Starters (Deprecated):
┌──────────────────────────┐   ┌────────────┐   ┌────────────────┐   ┌────────────────┐
│character-generator-starter│   │data-starter│   │security-starter│   │genefunk-starter│
└──────────────────────────┘   └────────────┘   └────────────────┘   └────────────────┘
```

### Module Dependency Matrix

| Module | Dependencies |
|--------|--------------|
| api | None |
| base | api |
| data | api, base |
| security | api, base |
| genefunk | api, base, data |
| monolith | data, genefunk, security (+ all transitive) |
| character-generator-starter | DEPRECATED (data, genefunk, security) |
| data-starter | DEPRECATED (data) |
| security-starter | DEPRECATED (security) |
| genefunk-starter | DEPRECATED (genefunk) |

### Compilation Order

```
1. api           (no dependencies)
2. base          (depends on api)
3. data          (depends on api, base)
   security      (depends on api, base)
4. genefunk      (depends on api, base, data)
5. monolith      (depends on data, genefunk, security)
```

### File Statistics

- **Total Kotlin Files**: 74
- **Total Java Files**: 0 (100% Kotlin migration complete)
- **Modules**: 10 (6 deprecated starters)
- **Active Modules**: 6 (api, base, data, security, genefunk, monolith)

---

---

**Last Updated**: 2025-11-14

**See Also**:
- [Deployment Documentation](DEPLOYMENT.md)
- [Accessibility Documentation](../ACCESSIBILITY.md)
- [Test Strategy](../TEST_STRATEGY.md)
- [Claude Code & S.C.R.U.M. Team](../CLAUDE.md)
- [ADR 0001: Kotlin Migration](adr/0001-kotlin-migration.md)
- [ADR 0002: Monolith Consolidation](adr/0002-monolith-consolidation.md)
