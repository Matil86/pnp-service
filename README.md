# PnP Service

A comprehensive Spring Boot microservice for pen-and-paper (PnP) role-playing games, providing character generation and management capabilities. This service uses RabbitMQ messaging, Firebase authentication, and supports multiple game systems including GeneFunk.

## Project Overview

The PnP Service is a consolidated Spring Boot application that provides character generation and management tools for tabletop role-playing games. It features a message-driven architecture using RabbitMQ for asynchronous processing, Firebase for OAuth2 authentication, and comprehensive REST APIs for game content management.

## Key Features

- **Character Generation**: Automated character creation for supported game systems
- **Multi-Game Support**: Modular architecture supporting different RPG systems (GeneFunk implemented)
- **Message Queue Architecture**: Asynchronous processing with RabbitMQ
- **OAuth2 Authentication**: Firebase-based user authentication and authorization
- **Localization**: Multi-language support for game content
- **REST APIs**: Comprehensive endpoints for character and locale management
- **Containerized Deployment**: Docker support with Amazon Corretto JVM

## Technology Stack

- **Java 24**: Runtime environment with Amazon Corretto
- **Kotlin 2.2.10**: Primary programming language
- **Spring Boot 3.5.5**: Application framework
- **Spring Security + OAuth2**: Authentication and authorization
- **Spring AMQP + RabbitMQ**: Message queue communication
- **Spring Data JPA**: Database persistence layer
- **H2 Database**: In-memory database for development
- **Firebase Admin SDK**: External authentication provider
- **Jackson**: JSON serialization/deserialization
- **KotlinLogging**: Structured logging
- **SpringDoc OpenAPI**: API documentation
- **Docker**: Containerization
- **Maven**: Build and dependency management

## Prerequisites

- **Java 24 JDK** (Amazon Corretto recommended)
- **Maven 3.8+**
- **RabbitMQ Server** (local or cloud instance)
- **Firebase Project** (for OAuth2 authentication)
- **Docker** (for containerized deployment)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd pnp-service
./mvnw clean compile
```

### 2. Environment Configuration

Create a `.env` file in the project root with the following variables:

```env
# RabbitMQ Configuration
RABBITMQ_HOST=your-rabbitmq-host
RABBITMQ_USERNAME=your-username
RABBITMQ_PASSWORD=your-password

# Database Configuration
DATASOURCE_NAME=database-name
DATASOURCE_USERNAME=db-username
DATASOURCE_PASSWORD=db-password

# Server Configuration
SERVER_PORT=8080

# Optional: CORS Configuration
DESKTOP_APP_URL=http://localhost:3000
```

### 3. Firebase Setup

Configure Firebase OAuth2 in `src/main/resources/application-oauth.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://securetoken.google.com/your-firebase-project-id
```

### 4. Run the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or with specific JVM arguments for Java 24
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED"
```

## Docker Deployment

### Build and Run with Docker

```bash
# Build the JAR file
./mvnw clean package

# Build Docker image
docker build -t pnp-service .

# Run container
docker run -p 8080:8080 \
  -e RABBITMQ_HOST=your-host \
  -e RABBITMQ_USERNAME=your-username \
  -e RABBITMQ_PASSWORD=your-password \
  -e SERVER_PORT=8080 \
  pnp-service
```

## API Documentation

The service provides several REST endpoints:

### Character Management
- `GET /api/character/locale` - Get localized character content
- Character generation endpoints (via RabbitMQ messaging)

### Locale Management
- `GET /locale?gameType={type}` - Get localization data for specific game type

### Health Check
- `GET /health` - Application health status

### Interactive API Documentation
When running, visit: `http://localhost:8080/swagger-ui.html` (if SpringDoc is configured)

## Message Queue Architecture

The service uses RabbitMQ for asynchronous processing with the following key queues:

### Character Operations
- `get.all.characters` - Retrieve user's characters
- `create.character` - Generate new character
- `delete.character` - Remove character

### User Management
- `get.internal.user` - Retrieve user by external ID
- `save.new.user` - Create/update user

### Game Data
- `get.genefunk.classes` - Retrieve GeneFunk character classes
- `get.genefunk.species` - Retrieve GeneFunk character species
- `get.all.language.keys` - Retrieve localization data

## Game System Support

### GeneFunk
Currently implemented RPG system featuring:
- Character generation with genome types
- Character classes and species
- Localized content support
- Full CRUD operations

### Adding New Game Systems
1. Create new service classes implementing the base interfaces
2. Add message listeners for the new game type
3. Configure localization resources
4. Update routing keys in `RoutingKeys.kt`

## Development Guidelines

### Code Style
- Use Kotlin for all new development
- Follow Spring Boot best practices
- Use `KotlinLogging.logger {}` for logging
- Implement comprehensive error handling
- Add KDoc documentation for public APIs

### Message Processing
- Always use `suspend` functions for async operations
- Wrap JSON processing in try-catch blocks
- Set `message.action = "finished"` when processing completes
- Include debug logging for received and processed messages

### Database
- Use JPA entities with proper annotations
- Implement repository pattern with Spring Data
- Handle nullable types appropriately in Kotlin

### Testing
```bash
# Run tests
./mvnw test

# Run with Kotest and MockK
./mvnw test -Dtest=*Test
```

## Configuration Profiles

The application uses multiple Spring profiles:

- `rabbitmq` - Message queue configuration
- `h2-database` - Database settings
- `logging` - Logging configuration
- `oauth` - OAuth2 authentication
- `genefunk-crb` - GeneFunk game data
- `genefunk-crb-locale` - GeneFunk localization

## Troubleshooting

### Java 24 Warnings
If you encounter sun.misc.Unsafe warnings, add JVM arguments:
```bash
--enable-native-access=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
```

### RabbitMQ Connection Issues
- Verify RabbitMQ server is running
- Check environment variables are correctly set
- Ensure virtual host matches username

### Authentication Problems
- Verify Firebase project configuration
- Check OAuth2 issuer URI in application-oauth.yml
- Ensure proper CORS configuration for frontend integration

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow the development guidelines
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.
