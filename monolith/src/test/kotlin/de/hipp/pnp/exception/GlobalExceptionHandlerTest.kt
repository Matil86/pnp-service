package de.hipp.pnp.exception

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

/**
 * Comprehensive tests for GlobalExceptionHandler.
 *
 * Tests all exception handlers and error response formatting.
 * Ensures proper HTTP status codes and security-aware error messages.
 */
class GlobalExceptionHandlerTest :
    StringSpec({

        fun createMockWebRequest(uri: String = "/api/test"): WebRequest {
            val request = mockk<WebRequest>()
            every { request.getDescription(false) } returns "uri=$uri"
            return request
        }

        "MethodArgumentNotValidException - Goku validates empty character name" {
            val handler = GlobalExceptionHandler()
            val bindingResult = mockk<BindingResult>()
            val fieldError1 = FieldError("character", "firstName", "First name is required")
            val fieldError2 = FieldError("character", "lastName", "Last name must not be empty")

            every { bindingResult.fieldErrors } returns listOf(fieldError1, fieldError2)

            val exception = MethodArgumentNotValidException(mockk(), bindingResult)
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleValidationException(exception, request)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body?.status shouldBe 400
            response.body?.error shouldBe "Validation Failed"
            response.body?.message shouldContain "validation failed"
            response.body?.path shouldBe "/api/characters"
            response.body?.validationErrors?.size shouldBe 2
            response.body?.validationErrors!! shouldContainKey "firstName"
            response.body?.validationErrors!! shouldContainKey "lastName"
        }

        "MethodArgumentNotValidException - Spider-Man checks email format validation" {
            val handler = GlobalExceptionHandler()
            val bindingResult = mockk<BindingResult>()
            val fieldError = FieldError("user", "email", "Email must be valid")

            every { bindingResult.fieldErrors } returns listOf(fieldError)

            val exception = MethodArgumentNotValidException(mockk(), bindingResult)
            val request = createMockWebRequest("/api/users")

            val response = handler.handleValidationException(exception, request)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body?.validationErrors?.get("email") shouldBe "Email must be valid"
        }

        "ConstraintViolationException - Tony Stark tests gameType bounds" {
            val handler = GlobalExceptionHandler()
            val violation = mockk<ConstraintViolation<*>>()

            every { violation.propertyPath.toString() } returns "gameType"
            every { violation.message } returns "Game type must not exceed 100"

            val violations = setOf(violation)
            val exception = ConstraintViolationException(violations)
            val request = createMockWebRequest("/api/characters/generate?gameType=999")

            val response = handler.handleConstraintViolationException(exception, request)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body?.status shouldBe 400
            response.body?.error shouldBe "Invalid Input"
            response.body?.message shouldContain "validation"
            response.body?.validationErrors?.get("gameType") shouldBe "Game type must not exceed 100"
        }

        "ConstraintViolationException - Batman validates negative character ID" {
            val handler = GlobalExceptionHandler()
            val violation = mockk<ConstraintViolation<*>>()

            every { violation.propertyPath.toString() } returns "characterId"
            every { violation.message } returns "Character ID must be a positive integer"

            val violations = setOf(violation)
            val exception = ConstraintViolationException(violations)
            val request = createMockWebRequest("/api/characters/-1")

            val response = handler.handleConstraintViolationException(exception, request)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body?.validationErrors?.get("characterId") shouldBe "Character ID must be a positive integer"
        }

        "MethodArgumentTypeMismatchException - Pikachu sends string instead of integer" {
            val handler = GlobalExceptionHandler()
            val exception = mockk<MethodArgumentTypeMismatchException>()

            every { exception.name } returns "characterId"
            every { exception.requiredType } returns Int::class.java
            every { exception.message } returns "Failed to convert value"

            val request = createMockWebRequest("/api/characters/pikachu")

            val response = handler.handleTypeMismatchException(exception, request)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body?.status shouldBe 400
            response.body?.error shouldBe "Invalid Input Type"
            response.body?.message shouldContain "characterId"
            response.body?.message shouldContain "int" // Java simpleName returns "int" not "Int"
            response.body?.path shouldBe "/api/characters/pikachu"
        }

        "MethodArgumentTypeMismatchException - Neo sends invalid gameType" {
            val handler = GlobalExceptionHandler()
            val exception = mockk<MethodArgumentTypeMismatchException>()

            every { exception.name } returns "gameType"
            every { exception.requiredType } returns Int::class.java
            every { exception.message } returns "Type mismatch"

            val request = createMockWebRequest("/api/characters/generate?gameType=matrix")

            val response = handler.handleTypeMismatchException(exception, request)

            response.body?.message shouldContain "gameType"
        }

        "JwtException - Naruto provides expired JWT token" {
            val handler = GlobalExceptionHandler()
            val exception = JwtException("Token has expired")
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleJwtException(exception, request)

            response.statusCode shouldBe HttpStatus.UNAUTHORIZED
            response.body?.status shouldBe 401
            response.body?.error shouldBe "Authentication Failed"
            response.body?.message shouldBe "Invalid or expired authentication token."
            response.body?.path shouldBe "/api/characters"
            response.body?.validationErrors shouldBe null
        }

        "JwtException - Gandalf provides malformed JWT" {
            val handler = GlobalExceptionHandler()
            val exception = JwtException("Malformed JWT token")
            val request = createMockWebRequest("/admin/users")

            val response = handler.handleJwtException(exception, request)

            response.statusCode shouldBe HttpStatus.UNAUTHORIZED
            // Security: Should not expose JWT details
            response.body?.message shouldNotBe "Malformed JWT token"
            response.body?.message shouldBe "Invalid or expired authentication token."
        }

        "BadCredentialsException - Wonder Woman checks wrong password" {
            val handler = GlobalExceptionHandler()
            val exception = BadCredentialsException("Bad credentials")
            val request = createMockWebRequest("/auth/login")

            val response = handler.handleBadCredentialsException(exception, request)

            response.statusCode shouldBe HttpStatus.UNAUTHORIZED
            response.body?.status shouldBe 401
            response.body?.error shouldBe "Authentication Failed"
            response.body?.message shouldBe "Authentication failed. Please check your credentials."
            // Security: Generic message to prevent username enumeration
            response.body?.message shouldNotBe "Bad credentials"
        }

        "AccessDeniedException - Vegeta tries to access admin endpoint" {
            val handler = GlobalExceptionHandler()
            val exception = AccessDeniedException("Access is denied")
            val request = createMockWebRequest("/admin/settings")

            val response = handler.handleAccessDeniedException(exception, request)

            response.statusCode shouldBe HttpStatus.FORBIDDEN
            response.body?.status shouldBe 403
            response.body?.error shouldBe "Access Denied"
            response.body?.message shouldBe "You do not have permission to access this resource."
            response.body?.path shouldBe "/admin/settings"
        }

        "AccessDeniedException - Frodo lacks ADMIN role" {
            val handler = GlobalExceptionHandler()
            val exception = AccessDeniedException("Insufficient permissions")
            val request = createMockWebRequest("/admin/characters")

            val response = handler.handleAccessDeniedException(exception, request)

            response.statusCode shouldBe HttpStatus.FORBIDDEN
            response.body?.error shouldBe "Access Denied"
        }

        "IllegalArgumentException - Deadpool provides invalid character data" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Character strength cannot be negative")
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleIllegalArgumentException(exception, request)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body?.status shouldBe 400
            response.body?.error shouldBe "Invalid Request"
            response.body?.message shouldBe "Character strength cannot be negative"
            response.body?.path shouldBe "/api/characters"
        }

        "IllegalArgumentException - Thanos sends null character name" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Character name is required")
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleIllegalArgumentException(exception, request)

            response.body?.message shouldBe "Character name is required"
        }

        "IllegalStateException - Hulk encounters database connection failure" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalStateException("Database connection is not available")
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleIllegalStateException(exception, request)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            response.body?.status shouldBe 500
            response.body?.error shouldBe "Internal Server Error"
            // Security: Generic message to prevent information leakage
            response.body?.message shouldBe "An internal error occurred. Please try again later."
            response.body?.message!! shouldNotContain "Database connection"
        }

        "IllegalStateException - Captain America faces service unavailable" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalStateException("RabbitMQ service is down")
            val request = createMockWebRequest("/api/characters/generate")

            val response = handler.handleIllegalStateException(exception, request)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            // Should not expose internal service details
            response.body?.message!! shouldNotContain "RabbitMQ"
        }

        "Exception - General exception for unhandled error (孫悟空 attack)" {
            val handler = GlobalExceptionHandler()
            val exception = RuntimeException("Unexpected Kamehameha error")
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleGlobalException(exception, request)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            response.body?.status shouldBe 500
            response.body?.error shouldBe "Internal Server Error"
            // Security: Should not expose internal exception details
            response.body?.message shouldBe "An unexpected error occurred. Please try again later."
            response.body?.message shouldNotBe "Unexpected Kamehameha error"
        }

        "Exception - NullPointerException caught by global handler" {
            val handler = GlobalExceptionHandler()
            val exception = NullPointerException("Null character reference")
            val request = createMockWebRequest("/api/characters/42")

            val response = handler.handleGlobalException(exception, request)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            response.body?.error shouldBe "Internal Server Error"
            // Security: Generic message
            response.body?.message!! shouldNotContain "Null"
        }

        "ErrorResponse - Loki verifies timestamp is present" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Test error")
            val request = createMockWebRequest("/api/test")

            val response = handler.handleIllegalArgumentException(exception, request)

            response.body?.timestamp shouldNotBe null
        }

        "ErrorResponse - Thor checks all required fields present" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Test error")
            val request = createMockWebRequest("/api/test")

            val response = handler.handleIllegalArgumentException(exception, request)

            val body = response.body
            body shouldNotBe null
            body?.timestamp shouldNotBe null
            body?.status shouldNotBe null
            body?.error shouldNotBe null
            body?.message shouldNotBe null
            body?.path shouldNotBe null
        }

        "Multiple validation errors - Black Widow validates complex object" {
            val handler = GlobalExceptionHandler()
            val bindingResult = mockk<BindingResult>()
            val errors =
                listOf(
                    FieldError("character", "firstName", "Required"),
                    FieldError("character", "lastName", "Required"),
                    FieldError("character", "age", "Must be positive"),
                    FieldError("character", "email", "Invalid format"),
                )

            every { bindingResult.fieldErrors } returns errors

            val exception = MethodArgumentNotValidException(mockk(), bindingResult)
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleValidationException(exception, request)

            response.body?.validationErrors?.size shouldBe 4
        }

        "Unicode handling - ピカチュウ tests Japanese character in error path" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Invalid ピカチュウ data")
            val request = createMockWebRequest("/api/characters/ピカチュウ")

            val response = handler.handleIllegalArgumentException(exception, request)

            response.body?.path shouldBe "/api/characters/ピカチュウ"
            response.body?.message shouldBe "Invalid ピカチュウ data"
        }

        "Emoji handling - Deadpool tests emoji in error message" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Invalid character data 🎲⚔️")
            val request = createMockWebRequest("/api/characters")

            val response = handler.handleIllegalArgumentException(exception, request)

            response.body?.message shouldContain "🎲"
            response.body?.message shouldContain "⚔️"
        }

        "Path extraction - removes uri= prefix correctly" {
            val handler = GlobalExceptionHandler()
            val exception = IllegalArgumentException("Test")
            val request = createMockWebRequest("/api/very/long/path/to/resource")

            val response = handler.handleIllegalArgumentException(exception, request)

            response.body?.path shouldBe "/api/very/long/path/to/resource"
            response.body?.path shouldNotBe "uri=/api/very/long/path/to/resource"
        }
    })
