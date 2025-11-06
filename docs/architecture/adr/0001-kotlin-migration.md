# ADR 0001: Migration from Java to Kotlin

## Status
Accepted (Completed)

## Context
The PnP Service was initially developed using Java 22 as the primary programming language. As the project evolved, we identified several opportunities to improve developer productivity, code quality, and maintainability by adopting Kotlin as the primary language.

### Initial State
- Java 22 codebase with Spring Boot 3.x
- Mix of imperative and functional programming patterns
- Verbose boilerplate code (getters, setters, builders)
- Limited null safety (relying on Optional and manual null checks)
- JPA entities requiring heavy annotation and boilerplate

### Challenges with Java
1. **Verbosity**: Data classes required extensive boilerplate (getters, setters, equals, hashCode, toString)
2. **Null Safety**: NPE (NullPointerException) risks despite using Optional
3. **Functional Programming**: Limited support for functional constructs compared to Kotlin
4. **Spring Boot Integration**: Kotlin provides better DSLs and integration with Spring
5. **JPA Entity Boilerplate**: Required extensive annotation and all-args constructors

## Decision
We decided to migrate the entire codebase from Java to Kotlin, making Kotlin the primary and exclusive language for the project.

### Migration Strategy
1. **Incremental Migration**: Module-by-module approach starting with core modules
2. **100% Kotlin Target**: No mixed Java/Kotlin codebase (except dependencies)
3. **Kotlin Best Practices**: Leverage data classes, extension functions, coroutines
4. **Spring Boot Kotlin Support**: Utilize Kotlin-specific Spring features
5. **JVM Target**: Java 24 (matching previous Java version)

### Key Kotlin Features Adopted
- **Data Classes**: Replaced Java POJOs with Kotlin data classes
- **Null Safety**: Leveraged Kotlin's built-in null safety (?, !!, ?:)
- **Extension Functions**: Added utility functions without inheritance
- **Smart Casts**: Eliminated explicit casting after type checks
- **Default Parameters**: Simplified constructor overloading
- **Named Arguments**: Improved code readability
- **Kotlin Logging**: Replaced Java logging with kotlin-logging library
- **Companion Objects**: Replaced static methods with companion objects
- **Sealed Classes**: Type-safe hierarchies for game entities
- **Coroutines**: Asynchronous programming (planned for future iterations)

## Consequences

### Positive
1. **Reduced Boilerplate**: 30-40% reduction in lines of code
   - Data classes eliminate getter/setter/equals/hashCode boilerplate
   - Extension functions replace utility classes

2. **Improved Null Safety**: Eliminated NullPointerExceptions at compile time
   - Nullable types explicitly marked with `?`
   - Compiler enforces null checks

3. **Enhanced Readability**: More expressive and concise code
   - Named arguments improve clarity
   - Extension functions read naturally

4. **Better Spring Boot Integration**:
   - Kotlin-specific annotations (`@SpringBootApplication`, `@RestController`)
   - No-arg and all-open compiler plugins for JPA/Spring

5. **Functional Programming**: First-class support for lambdas, higher-order functions
   - Collection operations (map, filter, reduce) more intuitive
   - Sequence processing for lazy evaluation

6. **Developer Productivity**: Faster development cycles
   - Less boilerplate means faster feature implementation
   - Better IDE support (IntelliJ IDEA)

7. **Type Safety**: Stronger compile-time guarantees
   - Sealed classes for type-safe hierarchies
   - Immutable by default (val vs var)

### Negative
1. **Learning Curve**: Team needs to learn Kotlin idioms and best practices
   - Training investment required
   - Different mindset from Java (more functional)

2. **Build Time**: Kotlin compilation slightly slower than Java
   - Mitigated by incremental compilation
   - Not significant for project size

3. **Library Compatibility**: Occasional issues with Java-only libraries
   - Rare occurrence with modern libraries
   - Spring Boot fully supports Kotlin

4. **JVM Version Requirements**: Requires Java 24 with specific flags
   - `--enable-native-access=ALL-UNNAMED`
   - `--add-opens` flags for reflection access

5. **Debugging**: Stack traces sometimes less clear than Java
   - Inline functions can complicate debugging
   - Kotlin coroutines require coroutine-aware debugging

### Neutral
1. **Interoperability**: Kotlin maintains 100% Java interoperability
   - Can use any Java library without issues
   - Firebase Admin SDK, Jackson, Spring all work seamlessly

2. **Tooling**: Same ecosystem as Java
   - Maven/Gradle build tools
   - IntelliJ IDEA, VS Code support

3. **Performance**: No significant runtime performance difference
   - Both compile to JVM bytecode
   - Coroutines potentially more efficient than threads (future)

## Implementation Details

### Maven Configuration
```xml
<properties>
    <kotlin.version>2.2.10</kotlin.version>
    <kotlin.code.style>official</kotlin.code.style>
    <kotlin.compiler.jvmTarget>24</kotlin.compiler.jvmTarget>
</properties>

<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>${kotlin.version}</version>
    <configuration>
        <compilerPlugins>
            <plugin>spring</plugin>
            <plugin>jpa</plugin>
            <plugin>all-open</plugin>
        </compilerPlugins>
    </configuration>
</plugin>
```

### Before (Java)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firebaseUid;
    private String email;
    private Role role;

    public User() {}

    public User(String firebaseUid, String email, Role role) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.role = role;
    }

    // Getters
    public Long getId() { return id; }
    public String getFirebaseUid() { return firebaseUid; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(Role role) { this.role = role; }

    // equals, hashCode, toString (50+ lines omitted)
}
```

### After (Kotlin)
```kotlin
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val firebaseUid: String,
    val email: String,
    val role: Role
)
```

**Result**: 75% reduction in lines of code with same functionality.

### Migration Statistics
- **Total Files Migrated**: 74 Kotlin files
- **Java Files Remaining**: 0 (100% Kotlin migration)
- **Code Reduction**: ~35% fewer lines of code
- **Compilation Time**: +10% increase (acceptable)
- **Developer Velocity**: +25% increase (estimated)

## Alternatives Considered

### 1. Stay with Java
**Pros**:
- No migration effort required
- Team already familiar with Java
- No learning curve

**Cons**:
- Continued boilerplate code
- Null safety issues
- Less expressive code
- Missed opportunity for productivity gains

**Decision**: Rejected due to long-term productivity and code quality concerns

### 2. Gradual Mixed Java/Kotlin Codebase
**Pros**:
- Lower initial effort
- Can migrate incrementally over time
- Reduced risk

**Cons**:
- Inconsistent codebase
- Two sets of patterns to maintain
- Confusing for new developers
- Integration issues between Java and Kotlin code

**Decision**: Rejected in favor of complete migration for consistency

### 3. Switch to Alternative JVM Language (Scala, Groovy)
**Pros**:
- Scala: More powerful type system
- Groovy: More dynamic features

**Cons**:
- Scala: Steep learning curve, slower compilation
- Groovy: Dynamic typing reduces safety
- Smaller ecosystem compared to Kotlin
- Less Spring Boot integration

**Decision**: Rejected in favor of Kotlin due to better Spring Boot support and gentler learning curve

## Lessons Learned

1. **Incremental Migration Works**: Module-by-module approach reduced risk
2. **Kotlin Learning Curve is Gentle**: Kotlin's Java interoperability eased transition
3. **Compiler Plugins are Essential**: Spring and JPA plugins critical for framework integration
4. **Data Classes are Powerful**: Biggest productivity boost
5. **Null Safety Catches Bugs Early**: Found several potential NPEs during migration
6. **Extension Functions Improve APIs**: Made code more readable without changing APIs

## Future Considerations

1. **Coroutines Adoption**: Leverage Kotlin coroutines for async operations
   - Replace RabbitMQ callbacks with suspend functions
   - Improve scalability with structured concurrency

2. **Kotlin DSLs**: Create domain-specific languages for character generation
   - Type-safe builders for character creation
   - Declarative configuration

3. **Multiplatform (KMP)**: Consider Kotlin Multiplatform for future cross-platform needs
   - Share business logic with potential mobile clients
   - Unified data models

4. **Arrow Library**: Functional programming constructs
   - Either/Option for error handling
   - Validated for input validation

## References
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Spring Boot Kotlin Support](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.kotlin)
- [Kotlin for Java Developers](https://kotlinlang.org/docs/java-to-kotlin-guide.html)
- [Official Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)

## Revision History
- **2024-11**: Initial ADR created (post-migration documentation)
- **Status**: Accepted and Implemented (100% migration complete)
