# PnP Service Testing Strategy

This document outlines the comprehensive testing philosophy, standards, and practices for the PnP Service project. Our testing approach emphasizes security, accessibility, and high code coverage while maintaining simplicity and avoiding framework bloat.

## Table of Contents
- [Testing Philosophy](#testing-philosophy)
- [Testing Framework](#testing-framework)
- [Test Organization](#test-organization)
- [Coverage Targets](#coverage-targets)
- [String Input Testing](#string-input-testing)
- [Security Testing](#security-testing)
- [Accessibility Testing](#accessibility-testing)
- [Performance Testing](#performance-testing)
- [Test Naming Conventions](#test-naming-conventions)
- [Framework Rules](#framework-rules)
- [CI/CD Integration](#cicd-integration)
- [Test Data Standards](#test-data-standards)
- [TDD Support](#tdd-support)

## Testing Philosophy

### Core Principles

1. **Kotest Only**: Single, consistent testing framework across all modules
2. **Security First**: OWASP Top 10 coverage required for all security-critical code
3. **Comprehensive String Testing**: Every string input tested with unicode, emoji, injection attacks
4. **High Coverage**: 90% line / 85% branch minimum for all modules
5. **Real-World Test Data**: Use creative, memorable names (Bilbo, Gandalf) instead of generic (user1, user2)
6. **Minimal Framework Bloat**: No JUnit, no Robolectric, no unnecessary dependencies
7. **Fast Feedback**: Tests should run quickly and fail clearly

### Quality Standards

Our testing standards are maintained by **Bruce Banner** (QA/Tester agent) who ensures:
- All tests use Kotest FunSpec style
- Comprehensive string input testing (unicode, SQL injection, XSS)
- OWASP Top 10 security test coverage
- Performance tests for critical paths
- 90% line / 85% branch coverage minimum

## Testing Framework

### Primary Framework: Kotest

**Version**: 5.7.2

**Why Kotest?**
- Native Kotlin support (no Java baggage)
- Multiple test styles (we use FunSpec)
- Excellent assertion DSL
- Property-based testing support
- Spring Boot integration via kotest-extensions-spring

**Core Dependencies**:
```kotlin
testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
testImplementation("io.kotest:kotest-assertions-core:5.7.2")
testImplementation("io.kotest:kotest-property:5.7.2")
testImplementation("io.kotest:kotest-extensions-spring:1.1.2")
```

### Mocking: MockK

**Version**: 1.13.8

**Why MockK?**
- Kotlin-first mocking library
- Better than Mockito for Kotlin code
- Clean DSL for mocking and verification
- Excellent coroutine support

**Dependency**:
```kotlin
testImplementation("io.mockk:mockk:1.13.8")
```

### Spring Boot Testing

For integration tests requiring Spring context:
```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
```

## Test Organization

### Directory Structure

```
module/
├── src/
│   ├── main/kotlin/
│   │   └── de/hipp/pnp/module/
│   └── test/kotlin/
│       └── de/hipp/pnp/module/
│           ├── ComponentTest.kt         (unit tests)
│           ├── ComponentIntegrationTest.kt (integration tests)
│           └── ComponentSecurityTest.kt    (security tests)
```

### Test Types

1. **Unit Tests**: Test individual components in isolation
   - File naming: `{Component}Test.kt`
   - Example: `UserServiceTest.kt`
   - Use MockK for dependencies

2. **Integration Tests**: Test components with real dependencies (database, Spring context)
   - File naming: `{Component}IntegrationTest.kt`
   - Example: `UserRepositoryIntegrationTest.kt`
   - Use Spring Boot Test support

3. **Security Tests**: Test security features (authentication, authorization, injection)
   - File naming: `{Component}SecurityTest.kt` or `StringInputSecurityTest.kt`
   - Example: `SecurityTest.kt`
   - Cover OWASP Top 10

4. **Accessibility Tests**: Test WCAG 2.1 Level AA compliance
   - File naming: `AccessibilityTest.kt`
   - Example: `base/src/test/kotlin/de/hipp/pnp/base/ui/AccessibilityTest.kt`
   - 100+ test scenarios

## Coverage Targets

### Jacoco Configuration

**Thresholds** (enforced in build):
- **Line Coverage**: 90% minimum
- **Branch Coverage**: 85% minimum

**Gradle Configuration**:
```kotlin
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                minimum = "0.85".toBigDecimal()
            }
        }
    }
}
```

### Current Coverage Status

| Module | Line Coverage | Branch Coverage | Status |
|--------|--------------|-----------------|--------|
| **security** | ~85-90% | ~85% | Near target |
| **base** | ~60% | ~55% | Needs work |
| **data** | ~20% | ~15% | Needs work |
| **api** | ~10% | ~5% | Needs work |
| **genefunk** | ~15% | ~10% | Needs work |
| **monolith** | ~10% | ~5% | Needs work |
| **Project-wide** | ~13.5% | ~10% | In progress |

### Coverage Goals

**Short-term (Next Sprint)**:
- Security module: 90%+ (fix 7 test failures)
- Base module: 85%+ (add 50+ tests)
- Data module: 70%+ (add repository tests)

**Medium-term (Next Quarter)**:
- All modules: 90%+ line / 85%+ branch
- Project-wide: 90%+ average

## String Input Testing

### Comprehensive String Testing Standard

**Every string input** in the application must be tested with:

#### 1. Empty and Null Cases
```kotlin
test("should handle empty string") {
    val result = service.processName("")
    result.isFailure shouldBe true
}

test("should handle null string") {
    val result = service.processName(null)
    result.isFailure shouldBe true
}
```

#### 2. Whitespace Cases
```kotlin
test("should handle whitespace-only string") {
    val result = service.processName("   ")
    result.isFailure shouldBe true
}
```

#### 3. Unicode Characters
```kotlin
test("should handle hiragana characters") {
    val result = service.processName("ひらがな")
    result.isSuccess shouldBe true
}

test("should handle katakana characters") {
    val result = service.processName("カタカナ")
    result.isSuccess shouldBe true
}

test("should handle emoji") {
    val result = service.processName("Test 🎮 User")
    result.isSuccess shouldBe true
}
```

#### 4. Security Injection Attempts
```kotlin
test("should handle SQL injection attempt") {
    val result = service.processName("admin' OR '1'='1")
    result.isSuccess shouldBe true
    // Should sanitize or reject, not execute
}

test("should handle XSS attempt") {
    val result = service.processName("<script>alert('xss')</script>")
    result.isSuccess shouldBe true
    // Should escape or reject
}
```

#### 5. Boundary Cases
```kotlin
test("should handle minimum length") {
    val result = service.processName("A")
    result.isSuccess shouldBe true
}

test("should handle maximum length") {
    val name = "A".repeat(255)
    val result = service.processName(name)
    result.isSuccess shouldBe true
}

test("should reject too long string") {
    val name = "A".repeat(256)
    val result = service.processName(name)
    result.isFailure shouldBe true
}
```

### String Testing Example

From `StringInputSecurityTest.kt`:
```kotlin
class StringInputSecurityTest : FunSpec({
    context("Email string validation") {
        test("should accept valid email") {
            val email = "bilbo.baggins@shire.me"
            validateEmail(email) shouldBe true
        }

        test("should reject SQL injection in email") {
            val email = "admin'--@example.com"
            validateEmail(email) shouldBe false
        }

        test("should handle unicode email") {
            val email = "gandalf@mirkwood.エルフ"
            validateEmail(email) shouldBe false // or true if supported
        }

        test("should handle emoji in email") {
            val email = "frodo🧙@shire.me"
            validateEmail(email) shouldBe false // typically invalid
        }
    }
})
```

## Security Testing

### OWASP Top 10 Coverage

Our security test suite covers all OWASP Top 10 2021 vulnerabilities:

#### A01: Broken Access Control
```kotlin
test("should prevent unauthorized user access") {
    val result = userService.getUser(userId = 1, requestingUserId = 2)
    result.isFailure shouldBe true
}

test("should allow admin access to all users") {
    val result = userService.getUser(userId = 1, requestingUserId = adminId)
    result.isSuccess shouldBe true
}
```

#### A02: Cryptographic Failures
```kotlin
test("should hash passwords before storage") {
    val user = userService.createUser("bilbo@shire.me", "my-password")
    user.password shouldNotBe "my-password"
    user.password should startWith("$2a$") // bcrypt
}
```

#### A03: Injection
```kotlin
test("should prevent SQL injection in user search") {
    val maliciousQuery = "'; DROP TABLE users; --"
    val result = userService.searchUsers(maliciousQuery)
    // Should not drop table, should sanitize input
    result.isSuccess shouldBe true
}

test("should prevent XSS in user name") {
    val xssPayload = "<script>alert('xss')</script>"
    val result = userService.createUser("test@example.com", xssPayload)
    result.user.name shouldNotContain "<script>"
}
```

#### A04: Insecure Design
```kotlin
test("should implement rate limiting on login") {
    repeat(10) {
        authService.login("user@example.com", "wrong-password")
    }
    val result = authService.login("user@example.com", "correct-password")
    result.isFailure shouldBe true // rate limited
}
```

#### A05: Security Misconfiguration
```kotlin
test("should set secure headers") {
    val headers = securityHeaders.getHeaders()
    headers["X-Frame-Options"] shouldBe "DENY"
    headers["X-Content-Type-Options"] shouldBe "nosniff"
    headers["X-XSS-Protection"] shouldBe "1; mode=block"
}
```

#### A06: Vulnerable Components
- Handled by OWASP Dependency Check in CI/CD
- No high/critical vulnerabilities allowed

#### A07: Authentication Failures
```kotlin
test("should reject invalid JWT token") {
    val invalidToken = "invalid.jwt.token"
    val result = authService.validateToken(invalidToken)
    result.isFailure shouldBe true
}

test("should reject expired JWT token") {
    val expiredToken = createExpiredToken()
    val result = authService.validateToken(expiredToken)
    result.isFailure shouldBe true
}
```

#### A08: Software and Data Integrity Failures
```kotlin
test("should validate Firebase token signature") {
    val tamperedToken = validToken.replace("signature", "invalid")
    val result = firebaseService.verifyToken(tamperedToken)
    result.isFailure shouldBe true
}
```

#### A09: Security Logging Failures
```kotlin
test("should log failed authentication attempts") {
    authService.login("user@example.com", "wrong-password")
    verify { logger.warn(match { it.contains("Failed login") }) }
}
```

#### A10: Server-Side Request Forgery (SSRF)
```kotlin
test("should prevent SSRF via redirect URL") {
    val maliciousUrl = "http://localhost:8080/admin/delete-all"
    val result = urlService.validateRedirect(maliciousUrl)
    result.isFailure shouldBe true
}
```

### Security Test Files

Current security test suite (3,652 lines):
1. `SecurityTest.kt` - Core security functionality
2. `FirebaseConfigurationTest.kt` - Firebase integration
3. `UserServiceTest.kt` - User management security
4. `UserRepositoryTest.kt` - Data access security
5. `UserListenerTest.kt` - Message queue security
6. `RoleTest.kt` - Role-based access control
7. `StringInputSecurityTest.kt` - Comprehensive string testing

## Accessibility Testing

### WCAG 2.1 Level AA Testing

All accessible components tested for WCAG compliance in `AccessibilityTest.kt`.

#### Test Coverage (100+ tests)

```kotlin
class AccessibilityTest : FunSpec({
    context("AccessibleButton") {
        test("should have required ARIA label") {
            val button = AccessibleButton(
                label = "Submit",
                onClick = "handleSubmit()",
                testId = "submit-btn"
            )
            button.toHtml() should contain("aria-label=\"Submit\"")
        }

        test("should reject empty label") {
            val result = AccessibleComponentFactory.createButton(
                label = "",
                onClick = "click()",
                testId = "test"
            )
            result.isFailure shouldBe true
        }

        test("should handle unicode in label") {
            val result = AccessibleComponentFactory.createButton(
                label = "送信 ボタン",
                onClick = "click()",
                testId = "test"
            )
            result.isSuccess shouldBe true
        }

        test("should handle emoji in label") {
            val result = AccessibleComponentFactory.createButton(
                label = "Submit 🚀",
                onClick = "click()",
                testId = "test"
            )
            result.isSuccess shouldBe true
        }
    }

    context("AccessibleInput") {
        test("should associate label with input") {
            val input = AccessibleInput(
                label = "Email",
                name = "email",
                type = InputType.EMAIL,
                testId = "email-input"
            )
            val html = input.toHtml()
            html should contain("for=\"email\"")
            html should contain("id=\"email\"")
        }

        test("should include error message in aria-describedby") {
            val input = AccessibleInput(
                label = "Email",
                name = "email",
                type = InputType.EMAIL,
                testId = "test",
                errorMessage = "Invalid email format"
            )
            input.toHtml() should contain("aria-describedby")
        }
    }
})
```

#### Accessibility Test Categories

1. **ARIA Attributes**: Verify all required ARIA attributes present
2. **Label Association**: Verify for/id matching for inputs
3. **Focus Management**: Verify tabindex and focus order (modal)
4. **Error Handling**: Verify error messages accessible
5. **Keyboard Navigation**: Document keyboard shortcuts
6. **Unicode Support**: Test with hiragana, katakana, emoji
7. **Factory Validation**: Test validation success/failure paths

## Performance Testing

### Load Testing

For critical endpoints, implement load tests:

```kotlin
class CharacterServicePerformanceTest : FunSpec({
    test("should generate 100 characters in under 10 seconds").config(timeout = 10.seconds) {
        val startTime = System.currentTimeMillis()

        repeat(100) {
            characterService.generateCharacter(gameType = 0)
        }

        val elapsed = System.currentTimeMillis() - startTime
        elapsed shouldBeLessThan 10_000
    }

    test("should handle 50 concurrent character generations") {
        val jobs = (1..50).map {
            async {
                characterService.generateCharacter(gameType = 0)
            }
        }

        val results = jobs.awaitAll()
        results.size shouldBe 50
        results.all { it.isSuccess } shouldBe true
    }
})
```

### Performance Benchmarks

Target performance metrics:
- **Character Generation**: < 500ms per character
- **API Response Time**: P95 < 200ms
- **Database Queries**: < 50ms per query
- **Memory Usage**: < 512MB under load

## Test Naming Conventions

### Test Style: Kotest FunSpec

```kotlin
class ComponentTest : FunSpec({
    context("describe the context or feature") {
        test("should describe expected behavior") {
            // Given
            val input = setupInput()

            // When
            val result = performAction(input)

            // Then
            result shouldBe expected
        }
    }
})
```

### Naming Guidelines

1. **Test Class Names**: `{Component}Test.kt`, `{Component}IntegrationTest.kt`
2. **Context Names**: Describe the feature or scenario
3. **Test Names**: Use "should" statements describing expected behavior
4. **Be Descriptive**: Long test names are fine if they're clear

### Good Examples

```kotlin
context("User authentication") {
    test("should successfully authenticate with valid JWT token") { }
    test("should reject authentication with expired JWT token") { }
    test("should reject authentication with invalid signature") { }
}

context("Character generation") {
    test("should generate character with random attributes") { }
    test("should generate character with specified genome") { }
    test("should reject invalid game type") { }
}
```

### Bad Examples

```kotlin
context("tests") {  // Too vague
    test("test1") { }  // Meaningless
    test("it works") { }  // What works?
}
```

## Framework Rules

### What to Use

- **Kotest**: All testing (unit, integration, security)
- **MockK**: All mocking
- **Spring Boot Test**: Integration tests requiring Spring context
- **Kotest Property Testing**: Property-based tests where appropriate

### What NOT to Use

- **JUnit**: No JUnit 4 or JUnit 5 (Kotest uses JUnit 5 engine internally, but write Kotest tests)
- **Mockito**: Use MockK instead
- **Robolectric**: Not needed (no Android UI testing)
- **AssertJ**: Use Kotest assertions instead
- **Hamcrest**: Use Kotest matchers instead

### Why These Rules?

1. **Consistency**: One framework across all tests
2. **Kotlin-First**: Kotest and MockK designed for Kotlin
3. **Less Bloat**: Fewer dependencies, faster builds
4. **Better DSL**: Kotest DSL more readable than JUnit
5. **Modern**: Kotest actively developed, JUnit 4 legacy

## CI/CD Integration

### GitHub Actions Workflow

```yaml
name: Test Suite

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 24
        uses: actions/setup-java@v3
        with:
          java-version: '24'
          distribution: 'corretto'

      - name: Run Tests
        run: ./gradlew test

      - name: Generate Coverage Report
        run: ./gradlew jacocoTestReport

      - name: Verify Coverage
        run: ./gradlew jacocoTestCoverageVerification

      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./build/reports/jacoco/test/jacocoTestReport.xml
```

### Quality Gates

Tests must pass before merge:
1. All tests passing (0 failures)
2. Coverage thresholds met (90% line / 85% branch)
3. No high/critical security vulnerabilities (OWASP Dependency Check)
4. Linting passes (ktlint 0 violations)

### Test Execution

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :security:test

# Run tests with coverage
./gradlew test jacocoTestReport

# Verify coverage thresholds
./gradlew jacocoTestCoverageVerification

# Run tests continuously
./gradlew test --continuous
```

## Test Data Standards

### Creative Test Data

Use memorable, creative names instead of generic data:

#### Good Test Data (Use These)

```kotlin
// Characters from fiction
val users = listOf(
    User(email = "bilbo.baggins@shire.me", name = "Bilbo Baggins"),
    User(email = "gandalf@istari.org", name = "Gandalf the Grey"),
    User(email = "aragorn@gondor.gov", name = "Aragorn"),
    User(email = "legolas@mirkwood.elf", name = "Legolas")
)

val characters = listOf(
    Character(name = "Neo", class = "Hacker", level = 99),
    Character(name = "Trinity", class = "Operator", level = 95),
    Character(name = "Morpheus", class = "Mentor", level = 100)
)
```

#### Bad Test Data (Avoid These)

```kotlin
// Generic, boring, forgettable
val users = listOf(
    User(email = "user1@example.com", name = "User 1"),
    User(email = "user2@example.com", name = "User 2"),
    User(email = "test@test.com", name = "Test User")
)
```

### Why Creative Test Data?

1. **Memorable**: Easier to remember in debugging
2. **Readable**: More enjoyable to read test code
3. **Distinctive**: Easier to distinguish between test cases
4. **Fun**: Makes testing more enjoyable

### Test Data Sources

- Lord of the Rings characters
- Star Wars characters
- Matrix characters
- Game of Thrones characters
- Marvel/DC superheroes
- Historical figures
- Mythological characters

## TDD Support

Bruce Banner (QA agent) supports Test-Driven Development:

### TDD Mode Workflow

1. **Red**: Write failing test first
2. **Green**: Implement minimum code to pass
3. **Refactor**: Improve code while keeping tests green

### TDD Example

```kotlin
// 1. RED - Write failing test
class CharacterServiceTest : FunSpec({
    test("should generate character with random genome") {
        val service = CharacterService()
        val character = service.generateCharacter(gameType = 0)

        character.genome shouldNotBe null
        character.genome.name shouldNotBe ""
    }
})

// Test fails - method doesn't exist

// 2. GREEN - Implement minimum code
class CharacterService {
    fun generateCharacter(gameType: Int): Character {
        return Character(
            genome = Genome(name = "Human")
        )
    }
}

// Test passes

// 3. REFACTOR - Improve implementation
class CharacterService(
    private val genomeService: GenomeService
) {
    fun generateCharacter(gameType: Int): Character {
        val genome = genomeService.randomGenome(gameType)
        return Character(genome = genome)
    }
}

// Test still passes
```

### When to Use TDD

- New features with clear requirements
- Bug fixes (write test that reproduces bug first)
- Refactoring (tests ensure behavior preserved)
- Complex logic (tests guide design)

## Testing Checklist

Before marking a feature complete, ensure:

- [ ] All unit tests written and passing
- [ ] Integration tests for external dependencies
- [ ] Security tests covering OWASP Top 10
- [ ] String input tests (empty, null, unicode, injection)
- [ ] Accessibility tests (if UI component)
- [ ] Performance tests (if critical path)
- [ ] Coverage meets 90% line / 85% branch
- [ ] All tests use Kotest (no JUnit)
- [ ] Test data is creative and memorable
- [ ] Test names are descriptive

## Future Testing Enhancements

### Short-Term
- Fix 8 test failures (1 base, 7 security)
- Expand coverage to all modules (data, api, genefunk)
- Add performance benchmarks for critical paths

### Medium-Term
- Chaos engineering tests (fault injection)
- Contract tests for API endpoints
- Mutation testing (PIT) for test effectiveness
- Automated accessibility testing in CI/CD

### Long-Term
- Property-based testing for all business logic
- Visual regression testing for UI components
- Load testing as part of CI/CD
- Continuous test coverage monitoring

## Resources

### Documentation
- [Kotest Documentation](https://kotest.io/)
- [MockK Documentation](https://mockk.io/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

### Security
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)

### Accessibility
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [ARIA Authoring Practices](https://www.w3.org/WAI/ARIA/apg/)

### Coverage
- [Jacoco Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

---

**Last Updated**: 2025-11-14
**Project**: PnP Service
**Owner**: Bruce Banner (QA/Tester)
**Maintained by**: Wong (Documentation Specialist)
