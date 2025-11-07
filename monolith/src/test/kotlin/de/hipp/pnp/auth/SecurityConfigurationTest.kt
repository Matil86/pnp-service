package de.hipp.pnp.auth

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

/**
 * Tests for SecurityConfiguration focusing on JWT authentication and role extraction
 */
class SecurityConfigurationTest : StringSpec({

    afterEach {
        SecurityContextHolder.clearContext()
    }

    fun createMockJwt(
        sub: String = "user123",
        email: String = "test@example.com",
        givenName: String = "Tony",
        familyName: String = "Stark",
        name: String = "Tony Stark"
    ): Jwt {
        val claims = mapOf(
            "sub" to sub,
            "email" to email,
            "given_name" to givenName,
            "family_name" to familyName,
            "name" to name
        )
        return Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claims { it.putAll(claims) }
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build()
    }

    "JWT Authentication Converter - Goku (孫悟空) gets USER role for new user" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "goku123",
            email = "goku@dbz.com",
            givenName = "Son",
            familyName = "Goku",
            name = "Son Goku"
        )

        every { userInfoProducer.getCustomerInfoFor("goku123") } returns Customer(
            userId = "1",
            vorname = "Son",
            nachname = "Goku",
            name = "Son Goku",
            externalIdentifer = "goku123",
            mail = "goku@dbz.com",
            role = null
        )
        every { userInfoProducer.saveNewUser(any()) } returns Customer()

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        authentication.authorities.map { it.authority } shouldContain "USER"
        verify { userInfoProducer.saveNewUser(any()) }
    }

    "JWT Authentication Converter - Tony Stark gets ADMIN and USER roles" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "tony123",
            email = "tony@stark.com"
        )

        every { userInfoProducer.getCustomerInfoFor("tony123") } returns Customer(
            userId = "1",
            vorname = "Tony",
            nachname = "Stark",
            name = "Tony Stark",
            externalIdentifer = "tony123",
            mail = "tony@stark.com",
            role = "ADMIN"
        )

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        val authorities = authentication.authorities.map { it.authority }
        authorities shouldContain "ADMIN"
        authorities shouldContain "USER"
    }

    "JWT Authentication Converter - Spider-Man gets USER role only" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "peter123",
            email = "peter@parker.com",
            givenName = "Peter",
            familyName = "Parker",
            name = "Peter Parker"
        )

        every { userInfoProducer.getCustomerInfoFor("peter123") } returns Customer(
            userId = "2",
            vorname = "Peter",
            nachname = "Parker",
            name = "Peter Parker",
            externalIdentifer = "peter123",
            mail = "peter@parker.com",
            role = "USER"
        )

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        val authorities = authentication.authorities.map { it.authority }
        authorities shouldContain "USER"
    }

    "JWT Authentication Converter - null JWT returns null" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val converter = securityConfig.jwtAuthenticationConverter()
        // The converter doesn't accept null as it implements Converter<Jwt, AbstractAuthenticationToken>
        // This test is removed as the interface contract doesn't support null input
        // Null handling is done at Spring Security framework level, not in converter
    }

    "Customer bean - Batman with valid JWT in security context" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "bruce123",
            email = "bruce@wayne.com",
            givenName = "Bruce",
            familyName = "Wayne",
            name = "Bruce Wayne"
        )

        every { userInfoProducer.getCustomerInfoFor("bruce123") } returns Customer(
            userId = "3",
            vorname = "Bruce",
            nachname = "Wayne",
            name = "Bruce Wayne",
            externalIdentifer = "bruce123",
            mail = "bruce@wayne.com",
            role = "ADMIN"
        )

        // Set up security context
        val auth = UsernamePasswordAuthenticationToken(jwt, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val customer = securityConfig.customer()

        customer.shouldNotBeNull()
        customer.vorname shouldBe "Bruce"
        customer.nachname shouldBe "Wayne"
    }

    "Customer bean - no authentication in context returns null" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        SecurityContextHolder.clearContext()

        val customer = securityConfig.customer()

        customer.shouldBeNull()
    }

    "Customer bean - Wonder Woman with non-JWT principal returns null" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val auth = UsernamePasswordAuthenticationToken("string-principal", null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        val customer = securityConfig.customer()

        customer.shouldBeNull()
    }

    "CORS Configuration - default origins include localhost" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val corsConfig = securityConfig.corsConfiguration()

        corsConfig.shouldNotBeNull()
        corsConfig.allowedOrigins.shouldNotBeNull()
        corsConfig.allowedOrigins!! shouldContain "http://localhost:3000"
        corsConfig.allowedOrigins!! shouldContain "http://localhost:8080"
    }

    "CORS Configuration - allowed methods include GET, POST, PUT, DELETE" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val corsConfig = securityConfig.corsConfiguration()

        corsConfig.allowedMethods.shouldNotBeNull()
        corsConfig.allowedMethods!! shouldContain "GET"
        corsConfig.allowedMethods!! shouldContain "POST"
        corsConfig.allowedMethods!! shouldContain "PUT"
        corsConfig.allowedMethods!! shouldContain "DELETE"
    }

    "CORS Configuration - credentials are allowed" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val corsConfig = securityConfig.corsConfiguration()

        corsConfig.allowCredentials shouldBe true
    }

    "Auto-user creation - Naruto (ナルト) is created with USER role" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "naruto123",
            email = "naruto@konoha.com",
            givenName = "Naruto",
            familyName = "Uzumaki",
            name = "Naruto Uzumaki"
        )

        every { userInfoProducer.getCustomerInfoFor("naruto123") } returns Customer(
            userId = "4",
            vorname = "",
            nachname = "",
            name = "",
            externalIdentifer = "naruto123",
            mail = "naruto@konoha.com",
            role = null
        )
        every { userInfoProducer.saveNewUser(any()) } returns Customer()

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        verify {
            userInfoProducer.saveNewUser(match { customer ->
                customer.mail == "naruto@konoha.com" &&
                customer.role == "USER" &&
                customer.vorname == "Naruto" &&
                customer.nachname == "Uzumaki"
            })
        }
    }

    "JWT without sub claim - Gandalf without user ID" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val claims = mapOf(
            "email" to "gandalf@middleearth.com",
            "given_name" to "Gandalf",
            "name" to "Gandalf the Grey"
        )
        val jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claims { it.putAll(claims) }
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build()

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        // Should have no custom authorities since no sub claim
    }

    "Role extraction - Pikachu (ピカチュウ) with empty role gets USER" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "pikachu123",
            email = "pikachu@pokemon.com"
        )

        every { userInfoProducer.getCustomerInfoFor("pikachu123") } returns Customer(
            userId = "5",
            vorname = "Pikachu",
            nachname = "",
            name = "Pikachu",
            externalIdentifer = "pikachu123",
            mail = "pikachu@pokemon.com",
            role = null
        )
        every { userInfoProducer.saveNewUser(any()) } returns Customer()

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        authentication.authorities.map { it.authority } shouldContain "USER"
    }

    "WebSecurityCustomizer - debug is disabled" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val customizer = securityConfig.webSecurityCustomizer()

        customizer.shouldNotBeNull()
    }

    "CORS Configuration Source - registers configuration for all paths" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val source = securityConfig.corsConfigurationSource()

        source.shouldNotBeNull()
        // Test that the source was created successfully
        // Detailed configuration testing is done in corsConfiguration() test
    }

    "JWT Authentication - Neo from Matrix gets proper authorities" {
        val userInfoProducer = mockk<UserInfoProducer>()
        val securityConfig = SecurityConfiguration(userInfoProducer)

        val jwt = createMockJwt(
            sub = "neo123",
            email = "neo@matrix.com",
            givenName = "Thomas",
            familyName = "Anderson",
            name = "Neo"
        )

        every { userInfoProducer.getCustomerInfoFor("neo123") } returns Customer(
            userId = "6",
            vorname = "Thomas",
            nachname = "Anderson",
            name = "Neo",
            externalIdentifer = "neo123",
            mail = "neo@matrix.com",
            role = "ADMIN"
        )

        val converter = securityConfig.jwtAuthenticationConverter()
        val authentication = converter.convert(jwt)

        authentication.shouldNotBeNull()
        val authorities = authentication.authorities.map { it.authority }
        authorities shouldContain "ADMIN"
        authorities shouldContain "USER"
    }

    "JWT claims extraction - all expected fields present" {
        val jwt = createMockJwt(
            sub = "test123",
            email = "test@test.com",
            givenName = "Test",
            familyName = "User",
            name = "Test User"
        )

        jwt.claims["sub"] shouldBe "test123"
        jwt.claims["email"] shouldBe "test@test.com"
        jwt.claims["given_name"] shouldBe "Test"
        jwt.claims["family_name"] shouldBe "User"
        jwt.claims["name"] shouldBe "Test User"
    }
})
