rootProject.name = "pnp-service"

// Active modules - supporting both monolith and microservice architectures
include(
    "api",
    "base",
    "data",
    "genefunk",
    "monolith",
    "security",
    "character-generator-starter",
    // Microservice starter modules (restored for dual architecture support)
    "data-starter",
    "genefunk-starter",
    "security-starter"
)
