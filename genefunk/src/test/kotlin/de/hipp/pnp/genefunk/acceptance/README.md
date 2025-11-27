# BDD Acceptance Tests

Behavior-Driven Development (BDD) tests written in business language that map directly to user stories and serve as **living documentation**.

## Purpose

These tests serve three critical functions:

1. **Automated Acceptance Criteria** - Verify user stories are complete
2. **Living Documentation** - Always up-to-date documentation of behavior
3. **Regression Prevention** - Catch breaking changes automatically

## Structure

Tests use Kotest `BehaviorSpec` with Given/When/Then syntax:

```kotlin
given("A player wants to create a character") {
    `when`("they provide valid character details") {
        // Arrange and Act

        then("character should have all required attributes") {
            // Assert
        }
    }
}
```

## Mapping to User Stories

Each test file maps to one or more user stories:

```markdown
## User Story: Character Creation

**As a** player
**I want to** create a character
**So that** I can start playing

### Acceptance Criteria
✓ Character has all six attributes (automated: CharacterCreationAcceptance.kt:35)
✓ Character starts at level 1 (automated: CharacterCreationAcceptance.kt:43)
✓ Character has starting equipment (automated: CharacterCreationAcceptance.kt:51)

### Test Status
Last Run: 2025-11-07 10:30
Status: PASSED
Coverage: 95%
```

## Running Acceptance Tests

```bash
# Run all acceptance tests
./gradlew test --tests "*Acceptance*"

# Run specific acceptance test
./gradlew test --tests "CharacterCreationAcceptance"

# Generate HTML report
./gradlew test
# Open: build/reports/tests/test/index.html
```

## Writing New Acceptance Tests

1. **Start with user story**:
   ```
   As a [role]
   I want to [goal]
   So that [benefit]
   ```

2. **Extract acceptance criteria**:
   - What must be true for story to be "done"?
   - What edge cases must be handled?
   - What accessibility requirements exist?

3. **Write BDD test**:
   ```kotlin
   given("User wants to [goal]") {
       `when`("they [action]") {
           then("system should [expected behavior]") {
               // Assert
           }
       }
   }
   ```

4. **Map tests to criteria**:
   - Add comments linking tests to specific acceptance criteria
   - Include line numbers for traceability
   - Update user story documentation

## Benefits

- **Faster acceptance** - Pepper reviews test coverage, not functionality
- **Self-documenting** - Tests explain what system does in plain language
- **Regression safety** - Changes that break stories fail tests
- **Team alignment** - Everyone understands what "done" means

## Example Test Structure

```kotlin
/**
 * Acceptance tests for [Feature Name]
 *
 * User Story: As a [role], I want to [goal] so that [benefit]
 */
class FeatureAcceptance : BehaviorSpec({
    given("Context or precondition") {
        `when`("Action occurs") {
            then("Expected outcome 1") { }
            then("Expected outcome 2") { }
        }

        `when`("Different action") {
            then("Different outcome") { }
        }
    }

    given("Error condition") {
        `when`("Invalid action attempted") {
            then("System should reject gracefully") { }
        }
    }
})
```

## Maintenance

- Update tests when acceptance criteria change
- Keep tests focused on behavior, not implementation
- Use descriptive names that read like sentences
- Include edge cases (empty, null, unicode, emoji)
- Add accessibility criteria from Vision

## Coverage Goals

- **100% of acceptance criteria** should have automated tests
- Tests should cover happy path, error cases, and edge cases
- Include string input variations (empty, whitespace, unicode, emoji)
- Include accessibility requirements from user stories
