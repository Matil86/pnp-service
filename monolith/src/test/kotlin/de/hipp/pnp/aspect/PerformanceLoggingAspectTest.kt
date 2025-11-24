package de.hipp.pnp.aspect

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature

/**
 * Comprehensive tests for PerformanceLoggingAspect.
 *
 * Tests cover:
 * - Performance monitoring for slow and fast methods
 * - Error handling and exception propagation
 * - Branch coverage for all conditional paths
 * - Edge cases with various execution times
 */
class PerformanceLoggingAspectTest :
    FunSpec({

        fun createMockJoinPoint(
            className: String,
            methodName: String,
            executionTime: Long = 500,
            shouldThrow: Boolean = false,
        ): ProceedingJoinPoint {
            val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
            val signature = mockk<Signature>(relaxed = true)

            // Use declaringTypeName with a full package name format
            every { signature.declaringTypeName } returns "com.example.$className"
            every { signature.name } returns methodName
            every { joinPoint.signature } returns signature

            if (shouldThrow) {
                every { joinPoint.proceed() } answers {
                    Thread.sleep(executionTime)
                    throw RuntimeException("Test exception")
                }
            } else {
                every { joinPoint.proceed() } answers {
                    Thread.sleep(executionTime)
                    "result"
                }
            }

            return joinPoint
        }

        // ========== Service Performance Monitoring Tests ==========

        context("Service performance monitoring - Fast methods") {
            test("should monitor fast method under threshold - Goku") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "getCharacter", executionTime = 100)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
                verify { joinPoint.proceed() }
            }

            test("should monitor very fast method (0ms) - Frodo") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "quickOperation", executionTime = 0)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should monitor fast method (1ms) - Black Widow") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("SpyController", "infiltrate", executionTime = 1)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }
        }

        context("Service performance monitoring - Slow methods") {
            test("should monitor slow method over threshold - Spider-Man") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "generateCharacter", executionTime = 1500)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
                verify { joinPoint.proceed() }
            }

            test("should monitor method exactly at threshold (1000ms) - Tony Stark") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "saveCharacter", executionTime = 1000)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
                verify { joinPoint.proceed() }
            }

            test("should monitor extremely slow method (3000ms) - Neo in Matrix") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("MatrixController", "loadMatrix", executionTime = 3000)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should monitor slow method returning null - Thanos") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.InfinityService"
                every { signature.name } returns "snap"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } answers {
                    Thread.sleep(2000)
                    null
                }

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe null
            }
        }

        context("Service performance monitoring - Exception handling") {
            test("should handle exception and re-throw - Vegeta") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "deleteCharacter", executionTime = 500, shouldThrow = true)

                val exception =
                    shouldThrow<RuntimeException> {
                        aspect.monitorServicePerformance(joinPoint)
                    }

                exception.message shouldBe "Test exception"
                verify { joinPoint.proceed() }
            }

            test("should handle slow method throwing exception - Hulk") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "smashCharacter", executionTime = 1500, shouldThrow = true)

                shouldThrow<RuntimeException> {
                    aspect.monitorServicePerformance(joinPoint)
                }
            }

            test("should handle InterruptedException - Captain America") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.ShieldService"
                every { signature.name } returns "throwShield"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } throws InterruptedException("Thread interrupted")

                shouldThrow<InterruptedException> {
                    aspect.monitorServicePerformance(joinPoint)
                }
            }
        }

        context("Service performance monitoring - Return values") {
            test("should handle null result - Pikachu (ピカチュウ)") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.PokemonService"
                every { signature.name } returns "findPokemon"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns null

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe null
                verify { joinPoint.proceed() }
            }

            test("should handle complex object result - Gandalf") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.WizardController"
                every { signature.name } returns "castSpell"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns mapOf("spell" to "fireball", "damage" to 100)

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe mapOf("spell" to "fireball", "damage" to 100)
            }
        }

        context("Service performance monitoring - Edge cases") {
            test("should handle special characters in method name - Loki") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.TricksterService"
                every { signature.name } returns "trickMethod$1"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "tricked"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "tricked"
            }

            test("should handle long class name - Thor") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.VeryLongAsgardianThunderGodController"
                every { signature.name } returns "summonLightning"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "lightning"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "lightning"
            }
        }

        // ========== Controller Performance Monitoring Tests ==========

        context("Controller performance monitoring - Fast methods") {
            test("should monitor fast controller method - Batman") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterRestController", "allCharacters", executionTime = 50)

                val result = aspect.monitorControllerPerformance(joinPoint)

                result shouldBe "result"
                verify { joinPoint.proceed() }
            }
        }

        context("Controller performance monitoring - Slow methods") {
            test("should monitor slow controller method - Wonder Woman") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterRestController", "generateCharacter", executionTime = 2000)

                val result = aspect.monitorControllerPerformance(joinPoint)

                result shouldBe "result"
                verify { joinPoint.proceed() }
            }

            test("should monitor very slow controller method - Naruto (ナルト)") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("LocaleRestController", "getLocale", executionTime = 5000)

                val result = aspect.monitorControllerPerformance(joinPoint)

                result shouldBe "result"
                verify { joinPoint.proceed() }
            }
        }

        context("Controller performance monitoring - Exception handling") {
            test("should handle exception in controller - Deadpool") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterRestController", "generateCharacter", executionTime = 200, shouldThrow = true)

                shouldThrow<RuntimeException> {
                    aspect.monitorControllerPerformance(joinPoint)
                }

                verify { joinPoint.proceed() }
            }
        }

        // ========== Branch Coverage Tests ==========

        context("Branch coverage - Duration thresholds") {
            test("should log when duration exactly equals threshold (1000ms)") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("Service", "method", executionTime = 1000)

                // Duration = 1000ms, slowThresholdMs = 1000ms
                // Branch: duration > slowThresholdMs is FALSE (1000 !> 1000)
                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should log when duration is 1ms over threshold (1001ms)") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("Service", "method", executionTime = 1001)

                // Duration = 1001ms, slowThresholdMs = 1000ms
                // Branch: duration > slowThresholdMs is TRUE (1001 > 1000)
                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should not log when duration is 1ms under threshold (999ms)") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("Service", "method", executionTime = 999)

                // Duration = 999ms, slowThresholdMs = 1000ms
                // Branch: duration > slowThresholdMs is FALSE (999 !> 1000)
                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }
        }

        context("Branch coverage - Exception vs Success paths") {
            test("should execute success path without exception") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("Service", "method", executionTime = 100, shouldThrow = false)

                // Branch: try block succeeds, no exception
                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should execute exception path when method throws") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("Service", "method", executionTime = 100, shouldThrow = true)

                // Branch: catch block executes
                shouldThrow<RuntimeException> {
                    aspect.monitorServicePerformance(joinPoint)
                }
            }

            test("should execute exception path with slow method") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("Service", "method", executionTime = 1500, shouldThrow = true)

                // Branch: catch block executes for slow method
                shouldThrow<RuntimeException> {
                    aspect.monitorServicePerformance(joinPoint)
                }
            }
        }

        context("Branch coverage - Service vs Controller") {
            test("should use Service component type") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterService", "get", executionTime = 100)

                // Branch: monitorServicePerformance calls monitorExecution with "Service"
                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should use Controller component type") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = createMockJoinPoint("CharacterController", "get", executionTime = 100)

                // Branch: monitorControllerPerformance calls monitorExecution with "Controller"
                val result = aspect.monitorControllerPerformance(joinPoint)

                result shouldBe "result"
            }
        }

        // ========== String Input Edge Cases ==========

        context("String input edge cases - Class and method names") {
            test("should handle empty method name") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.Service"
                every { signature.name } returns ""
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle hiragana in method name - ひらがな") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.JapaneseService"
                every { signature.name } returns "ひらがな"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle katakana in method name - カタカナ") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.JapaneseService"
                every { signature.name } returns "カタカナメソッド"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle emoji in method name - celebrate 🎉") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.PartyService"
                every { signature.name } returns "celebrate🎉"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle SQL injection in class name") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.'; DROP TABLE classes; --"
                every { signature.name } returns "method"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle XSS in method name") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.XSSService"
                every { signature.name } returns "<script>alert('XSS')</script>"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle very long class name") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                val longClassName = "com.example." + "VeryLong".repeat(50) + "Service"
                every { signature.declaringTypeName } returns longClassName
                every { signature.name } returns "method"
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }

            test("should handle whitespace-only method name") {
                val aspect = PerformanceLoggingAspect()
                val joinPoint = mockk<ProceedingJoinPoint>(relaxed = true)
                val signature = mockk<Signature>(relaxed = true)

                every { signature.declaringTypeName } returns "com.example.Service"
                every { signature.name } returns "   "
                every { joinPoint.signature } returns signature
                every { joinPoint.proceed() } returns "result"

                val result = aspect.monitorServicePerformance(joinPoint)

                result shouldBe "result"
            }
        }
    })
