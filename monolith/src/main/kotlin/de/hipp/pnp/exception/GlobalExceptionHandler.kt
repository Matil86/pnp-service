package de.hipp.pnp.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant

/**
 * Global exception handler for REST controllers.
 * Provides centralized exception handling with proper HTTP status codes and security-aware error messages.
 * Follows OWASP guidelines to prevent information leakage through error messages.
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = KotlinLogging.logger {}

    /**
     * Handles validation errors from @Valid and @Validated annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Validation error: ${ex.message}" }

        val errors =
            ex.bindingResult.fieldErrors.associate {
                it.field to (it.defaultMessage ?: "Validation failed")
            }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Validation Failed",
                message = "Input validation failed. Please check your request data.",
                path = request.getDescription(false).removePrefix("uri="),
                validationErrors = errors,
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Handles constraint violation exceptions from path variables and request parameters.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Constraint violation: ${ex.message}" }

        val errors =
            ex.constraintViolations.associate {
                it.propertyPath.toString() to it.message
            }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Invalid Input",
                message = "Request parameters failed validation.",
                path = request.getDescription(false).removePrefix("uri="),
                validationErrors = errors,
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Handles type mismatch errors (e.g., sending string when integer expected).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Type mismatch error: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Invalid Input Type",
                message = "Parameter '${ex.name}' has invalid type. Expected ${ex.requiredType?.simpleName}.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Handles JWT token validation failures.
     * SECURITY: Returns generic error to avoid information leakage.
     */
    @ExceptionHandler(JwtException::class)
    fun handleJwtException(
        ex: JwtException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error { "JWT authentication failed: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.UNAUTHORIZED.value(),
                error = "Authentication Failed",
                message = "Invalid or expired authentication token.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    /**
     * Handles bad credentials errors.
     * SECURITY: Returns generic error to avoid username enumeration.
     */
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        ex: BadCredentialsException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error { "Bad credentials: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.UNAUTHORIZED.value(),
                error = "Authentication Failed",
                message = "Authentication failed. Please check your credentials.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    /**
     * Handles access denied errors (insufficient permissions).
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error { "Access denied: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.FORBIDDEN.value(),
                error = "Access Denied",
                message = "You do not have permission to access this resource.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    /**
     * Handles illegal argument exceptions.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Illegal argument: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Invalid Request",
                message = ex.message ?: "Invalid request parameters.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Handles illegal state exceptions.
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        ex: IllegalStateException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Illegal state: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Internal Server Error",
                message = "An internal error occurred. Please try again later.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    /**
     * Handles all other unhandled exceptions.
     * SECURITY: Returns generic error message to prevent information leakage.
     */
    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unhandled exception: ${ex.message}" }

        val errorResponse =
            ErrorResponse(
                timestamp = Instant.now(),
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Internal Server Error",
                message = "An unexpected error occurred. Please try again later.",
                path = request.getDescription(false).removePrefix("uri="),
            )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}

/**
 * Standard error response structure.
 * Provides consistent error format across all endpoints.
 *
 * @property timestamp When the error occurred
 * @property status HTTP status code
 * @property error Error type/category
 * @property message User-friendly error message
 * @property path Request path that caused the error
 * @property validationErrors Optional map of field-specific validation errors
 */
data class ErrorResponse(
    val timestamp: Instant,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val validationErrors: Map<String, String>? = null,
)
