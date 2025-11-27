#!/bin/bash
#
# Entity Generator Script
# Generates a complete CRUD stack: Entity, Repository, Service, Controller, and Tests
#
# Usage: ./scripts/generate-entity.sh <EntityName> <module>
# Example: ./scripts/generate-entity.sh Task monolith
#

set -e

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <EntityName> <module>"
    echo "Example: $0 Task monolith"
    exit 1
fi

ENTITY_NAME=$1
MODULE=$2
ENTITY_LOWER=$(echo "$ENTITY_NAME" | awk '{print tolower($0)}')
ENTITY_PLURAL="${ENTITY_LOWER}s"

BASE_PACKAGE="de.hipp.pnp.${MODULE}"
SRC_DIR="${MODULE}/src/main/kotlin/de/hipp/pnp/${MODULE}"
TEST_DIR="${MODULE}/src/test/kotlin/de/hipp/pnp/${MODULE}"

echo "🚀 Generating entity stack for: $ENTITY_NAME in module: $MODULE"
echo "=================================================="

# Create directories if they don't exist
mkdir -p "${SRC_DIR}/entity"
mkdir -p "${SRC_DIR}/repository"
mkdir -p "${SRC_DIR}/service"
mkdir -p "${SRC_DIR}/rest"
mkdir -p "${TEST_DIR}/service"
mkdir -p "${TEST_DIR}/rest"

# ========== 1. Generate Entity ==========
echo "📦 Generating Entity: ${ENTITY_NAME}Entity.kt"
cat > "${SRC_DIR}/entity/${ENTITY_NAME}Entity.kt" <<EOF
package ${BASE_PACKAGE}.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "${ENTITY_PLURAL}")
data class ${ENTITY_NAME}Entity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    var createdBy: String? = null
)
EOF

# ========== 2. Generate Repository ==========
echo "🗄️  Generating Repository: ${ENTITY_NAME}Repository.kt"
cat > "${SRC_DIR}/repository/${ENTITY_NAME}Repository.kt" <<EOF
package ${BASE_PACKAGE}.repository

import ${BASE_PACKAGE}.entity.${ENTITY_NAME}Entity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ${ENTITY_NAME}Repository : JpaRepository<${ENTITY_NAME}Entity, Long> {
    fun findByName(name: String): List<${ENTITY_NAME}Entity>
    fun findByCreatedBy(createdBy: String): List<${ENTITY_NAME}Entity>
    fun existsByName(name: String): Boolean
}
EOF

# ========== 3. Generate Service ==========
echo "⚙️  Generating Service: ${ENTITY_NAME}Service.kt"
cat > "${SRC_DIR}/service/${ENTITY_NAME}Service.kt" <<EOF
package ${BASE_PACKAGE}.service

import ${BASE_PACKAGE}.entity.${ENTITY_NAME}Entity
import ${BASE_PACKAGE}.repository.${ENTITY_NAME}Repository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class ${ENTITY_NAME}Service(
    private val repository: ${ENTITY_NAME}Repository
) {
    @Transactional(readOnly = true)
    fun findAll(): List<${ENTITY_NAME}Entity> {
        logger.info { "Finding all ${ENTITY_PLURAL}" }
        return repository.findAll()
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): ${ENTITY_NAME}Entity {
        logger.info { "Finding ${ENTITY_LOWER} by id: \$id" }
        return repository.findById(id)
            .orElseThrow { EntityNotFoundException("${ENTITY_NAME} not found with id: \$id") }
    }

    @Transactional(readOnly = true)
    fun findByName(name: String): List<${ENTITY_NAME}Entity> {
        require(name.isNotBlank()) { "Name cannot be blank" }
        logger.info { "Finding ${ENTITY_PLURAL} by name: \$name" }
        return repository.findByName(name)
    }

    @Transactional
    fun create(entity: ${ENTITY_NAME}Entity): ${ENTITY_NAME}Entity {
        require(entity.name.isNotBlank()) { "Name cannot be blank" }
        require(entity.id == null) { "Cannot create entity with existing id" }

        if (repository.existsByName(entity.name)) {
            throw IllegalArgumentException("${ENTITY_NAME} with name '\${entity.name}' already exists")
        }

        logger.info { "Creating new ${ENTITY_LOWER}: \${entity.name}" }
        entity.createdAt = LocalDateTime.now()
        entity.updatedAt = LocalDateTime.now()

        return repository.save(entity)
    }

    @Transactional
    fun update(id: Long, entity: ${ENTITY_NAME}Entity): ${ENTITY_NAME}Entity {
        require(entity.name.isNotBlank()) { "Name cannot be blank" }

        val existing = findById(id)
        logger.info { "Updating ${ENTITY_LOWER}: \$id" }

        existing.name = entity.name
        existing.description = entity.description
        existing.updatedAt = LocalDateTime.now()

        return repository.save(existing)
    }

    @Transactional
    fun delete(id: Long) {
        val entity = findById(id)
        logger.info { "Deleting ${ENTITY_LOWER}: \$id" }
        repository.delete(entity)
    }

    class EntityNotFoundException(message: String) : RuntimeException(message)
}
EOF

# ========== 4. Generate REST Controller ==========
echo "🌐 Generating Controller: ${ENTITY_NAME}Controller.kt"
cat > "${SRC_DIR}/rest/${ENTITY_NAME}Controller.kt" <<EOF
package ${BASE_PACKAGE}.rest

import ${BASE_PACKAGE}.entity.${ENTITY_NAME}Entity
import ${BASE_PACKAGE}.service.${ENTITY_NAME}Service
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/${ENTITY_PLURAL}")
class ${ENTITY_NAME}Controller(
    private val service: ${ENTITY_NAME}Service
) {
    @GetMapping
    fun findAll(): ResponseEntity<List<${ENTITY_NAME}Entity>> {
        logger.info { "GET /api/${ENTITY_PLURAL} - Find all ${ENTITY_PLURAL}" }
        val entities = service.findAll()
        return ResponseEntity.ok(entities)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<${ENTITY_NAME}Entity> {
        logger.info { "GET /api/${ENTITY_PLURAL}/\$id - Find ${ENTITY_LOWER} by id" }
        return try {
            val entity = service.findById(id)
            ResponseEntity.ok(entity)
        } catch (e: ${ENTITY_NAME}Service.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun findByName(@RequestParam name: String): ResponseEntity<List<${ENTITY_NAME}Entity>> {
        logger.info { "GET /api/${ENTITY_PLURAL}/search?name=\$name" }
        return try {
            val entities = service.findByName(name)
            ResponseEntity.ok(entities)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping
    fun create(@RequestBody entity: ${ENTITY_NAME}Entity): ResponseEntity<${ENTITY_NAME}Entity> {
        logger.info { "POST /api/${ENTITY_PLURAL} - Create new ${ENTITY_LOWER}" }
        return try {
            val created = service.create(entity)
            ResponseEntity.status(HttpStatus.CREATED).body(created)
        } catch (e: IllegalArgumentException) {
            logger.warn { "Bad request: \${e.message}" }
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody entity: ${ENTITY_NAME}Entity
    ): ResponseEntity<${ENTITY_NAME}Entity> {
        logger.info { "PUT /api/${ENTITY_PLURAL}/\$id - Update ${ENTITY_LOWER}" }
        return try {
            val updated = service.update(id, entity)
            ResponseEntity.ok(updated)
        } catch (e: ${ENTITY_NAME}Service.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info { "DELETE /api/${ENTITY_PLURAL}/\$id - Delete ${ENTITY_LOWER}" }
        return try {
            service.delete(id)
            ResponseEntity.noContent().build()
        } catch (e: ${ENTITY_NAME}Service.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}
EOF

# ========== 5. Generate Service Tests ==========
echo "🧪 Generating Service Tests: ${ENTITY_NAME}ServiceTest.kt"
cat > "${TEST_DIR}/service/${ENTITY_NAME}ServiceTest.kt" <<EOF
package ${BASE_PACKAGE}.service

import ${BASE_PACKAGE}.entity.${ENTITY_NAME}Entity
import ${BASE_PACKAGE}.repository.${ENTITY_NAME}Repository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.util.*

class ${ENTITY_NAME}ServiceTest : StringSpec({
    lateinit var repository: ${ENTITY_NAME}Repository
    lateinit var service: ${ENTITY_NAME}Service

    beforeTest {
        repository = mockk()
        service = ${ENTITY_NAME}Service(repository)
    }

    afterTest {
        clearAllMocks()
    }

    "should find all ${ENTITY_PLURAL}" {
        val entities = listOf(
            ${ENTITY_NAME}Entity(id = 1, name = "Test 1"),
            ${ENTITY_NAME}Entity(id = 2, name = "Test 2")
        )
        every { repository.findAll() } returns entities

        val result = service.findAll()

        result shouldHaveSize 2
        verify(exactly = 1) { repository.findAll() }
    }

    "should find ${ENTITY_LOWER} by id" {
        val entity = ${ENTITY_NAME}Entity(id = 1, name = "Test")
        every { repository.findById(1) } returns Optional.of(entity)

        val result = service.findById(1)

        result shouldBe entity
        verify(exactly = 1) { repository.findById(1) }
    }

    "should throw exception when ${ENTITY_LOWER} not found" {
        every { repository.findById(999) } returns Optional.empty()

        shouldThrow<${ENTITY_NAME}Service.EntityNotFoundException> {
            service.findById(999)
        }
    }

    "should create new ${ENTITY_LOWER}" {
        val entity = ${ENTITY_NAME}Entity(name = "New ${ENTITY_NAME}")
        every { repository.existsByName("New ${ENTITY_NAME}") } returns false
        every { repository.save(any()) } returns entity.copy(id = 1)

        val result = service.create(entity)

        result.id shouldNotBe null
        verify(exactly = 1) { repository.save(any()) }
    }

    "should reject creation with blank name" {
        val entity = ${ENTITY_NAME}Entity(name = "")

        shouldThrow<IllegalArgumentException> {
            service.create(entity)
        }

        verify(exactly = 0) { repository.save(any()) }
    }

    "should handle empty string name" {
        val entity = ${ENTITY_NAME}Entity(name = "")

        shouldThrow<IllegalArgumentException> {
            service.create(entity)
        }
    }

    "should handle whitespace-only name" {
        val entity = ${ENTITY_NAME}Entity(name = "   ")

        shouldThrow<IllegalArgumentException> {
            service.create(entity)
        }
    }

    "should handle hiragana characters in name" {
        val entity = ${ENTITY_NAME}Entity(name = "テスト")
        every { repository.existsByName("テスト") } returns false
        every { repository.save(any()) } returns entity.copy(id = 1)

        val result = service.create(entity)

        result.name shouldBe "テスト"
    }

    "should handle emoji in name" {
        val entity = ${ENTITY_NAME}Entity(name = "Test ✓")
        every { repository.existsByName("Test ✓") } returns false
        every { repository.save(any()) } returns entity.copy(id = 1)

        val result = service.create(entity)

        result.name shouldBe "Test ✓"
    }

    "should update existing ${ENTITY_LOWER}" {
        val existing = ${ENTITY_NAME}Entity(id = 1, name = "Old Name")
        val updated = ${ENTITY_NAME}Entity(name = "New Name")

        every { repository.findById(1) } returns Optional.of(existing)
        every { repository.save(any()) } returns existing.copy(name = "New Name")

        val result = service.update(1, updated)

        result.name shouldBe "New Name"
        verify(exactly = 1) { repository.save(any()) }
    }

    "should delete ${ENTITY_LOWER}" {
        val entity = ${ENTITY_NAME}Entity(id = 1, name = "Test")
        every { repository.findById(1) } returns Optional.of(entity)
        every { repository.delete(entity) } just Runs

        service.delete(1)

        verify(exactly = 1) { repository.delete(entity) }
    }
})
EOF

echo ""
echo "✅ Entity stack generated successfully!"
echo "=================================================="
echo "Files created:"
echo "  - ${SRC_DIR}/entity/${ENTITY_NAME}Entity.kt"
echo "  - ${SRC_DIR}/repository/${ENTITY_NAME}Repository.kt"
echo "  - ${SRC_DIR}/service/${ENTITY_NAME}Service.kt"
echo "  - ${SRC_DIR}/rest/${ENTITY_NAME}Controller.kt"
echo "  - ${TEST_DIR}/service/${ENTITY_NAME}ServiceTest.kt"
echo ""
echo "📝 Next steps:"
echo "  1. Review and customize the generated code"
echo "  2. Add database migration (if using Flyway/Liquibase)"
echo "  3. Run tests: ./gradlew test"
echo "  4. Add any additional business logic"
echo ""
