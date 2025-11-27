rootProject.name = "pnp-service"

// Active modules
include(
    "api",
    "base",
    "data",
    "genefunk",
    "monolith",
    "security",
    "character-generator-starter"
)

// Deprecated microservice starter modules removed (replaced by monolith):
// - data-starter (replaced by monolith)
// - genefunk-starter (replaced by monolith)
// - security-starter (replaced by monolith)
// See ADR 0002: Monolith Consolidation for details
