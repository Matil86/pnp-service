# PnP Service

A comprehensive service for pen and paper (PnP) role-playing games, providing character generation capabilities based on the 5e license. This project uses Spring Boot and RabbitMQ to create a modular service architecture for various game systems.

## Project Overview

The PnP Service is designed to provide character generation and management tools for tabletop role-playing games. It's built with a modular architecture to support multiple game systems, with GeneFunk being the first implementation (currently a work in progress).

## Modules

The project consists of the following modules:

- **api**: Common API definitions and interfaces
- **base**: Core functionality and shared components
- **character-generator-starter**: Character generation service with desktop app integration
- **genefunk**: Implementation for the GeneFunk game system
- **genefunk-starter**: Starter module for GeneFunk
- **genefunk-bootstrap**: Bootstrap configuration for GeneFunk
- **data**: Data persistence and management
- **data-starter**: Starter module for data services
- **security**: Authentication and authorization
- **security-starter**: Starter module for security configuration

## Technologies

- **Java 22**: Core programming language
- **Kotlin 2.1.10**: Additional programming language for certain components
- **Spring Boot 3.4.3**: Application framework
- **Spring AMQP/RabbitMQ**: Message queue for service communication
- **Spring Security/OAuth2**: Authentication and authorization
- **Jackson**: JSON serialization/deserialization

## Setup and Installation

### Prerequisites

- Java 22 JDK
- Maven
- RabbitMQ server

### Building the Project

```bash
mvn clean install
```

### Configuration

Each module has its own configuration in `application.yml`. Key configuration areas include:

#### OAuth2 Configuration (for Character Generator)

- `GOOGLE_CLIENT_ID`: Your Google OAuth client ID
- `GOOGLE_CLIENT_SECRET`: Your Google OAuth client secret
- `OAUTH2_REDIRECT_URI`: The redirect URI for OAuth2 authentication
  - Example: `http://localhost:8080/login/oauth2/code/google`

#### Desktop App Integration

For integrating with a desktop application, see the detailed instructions in the [Character Generator Starter README](character-generator-starter/README.md).

## Usage

### Starting the Services

Each module can be run independently:

```bash
cd <module-name>-starter
mvn spring-boot:run
```

### API Endpoints

- Character generation: `/api/character/**`
- Authentication: `/auth/**`
- Test endpoints: `/test/**`

## Current Implementations

- **GeneFunk**: Work in progress

## Roadmap

- Data Service
  - Move data storage functionality to the Data Service
- Complete content transfer for GeneFunk
- Integration of additional game systems

## Testing

You can use the test endpoints to verify functionality:

```bash
# Test public endpoint
curl http://localhost:8080/test/public

# Test authenticated endpoint (requires valid session or token)
curl http://localhost:8080/test/auth -H "Authorization: Bearer YOUR_TOKEN"
```

## License

This project is based on the 5e license. See the [LICENSE](LICENSE) file for details.
