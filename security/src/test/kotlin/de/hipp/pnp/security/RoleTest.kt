package de.hipp.pnp.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Comprehensive test suite for Role enum.
 *
 * Coverage includes:
 * - Enum value verification
 * - toString() behavior
 * - Enum comparison
 * - All enum constants
 */
class RoleTest :
    FunSpec({

        context("Role Enum - Basic Functionality") {
            test("should have ADMIN role") {
                val role = Role.ADMIN

                role shouldNotBe null
                role.toString() shouldBe "ADMIN"
            }

            test("should have ANONYMOUS role") {
                val role = Role.ANONYMOUS

                role shouldNotBe null
                role.toString() shouldBe "ANONYMOUS"
            }

            test("should have USER role") {
                val role = Role.USER

                role shouldNotBe null
                role.toString() shouldBe "USER"
            }

            test("should have exactly 3 role values") {
                val roles = Role.entries

                roles shouldHaveSize 3
            }

            test("should contain all expected roles") {
                val roles = Role.entries

                roles shouldContain Role.ADMIN
                roles shouldContain Role.ANONYMOUS
                roles shouldContain Role.USER
            }
        }

        context("Role Enum - toString() Method") {
            test("ADMIN toString should return ADMIN") {
                Role.ADMIN.toString() shouldBe "ADMIN"
            }

            test("ANONYMOUS toString should return ANONYMOUS") {
                Role.ANONYMOUS.toString() shouldBe "ANONYMOUS"
            }

            test("USER toString should return USER") {
                Role.USER.toString() shouldBe "USER"
            }

            test("toString should return same value as enum name for ADMIN") {
                Role.ADMIN.toString() shouldBe Role.ADMIN.name
            }

            test("toString should return same value as enum name for USER") {
                Role.USER.toString() shouldBe Role.USER.name
            }

            test("toString should return same value as enum name for ANONYMOUS") {
                Role.ANONYMOUS.toString() shouldBe Role.ANONYMOUS.name
            }
        }

        context("Role Enum - Comparison and Equality") {
            test("ADMIN should equal itself") {
                val role1 = Role.ADMIN
                val role2 = Role.ADMIN

                role1 shouldBe role2
            }

            test("USER should equal itself") {
                val role1 = Role.USER
                val role2 = Role.USER

                role1 shouldBe role2
            }

            test("ANONYMOUS should equal itself") {
                val role1 = Role.ANONYMOUS
                val role2 = Role.ANONYMOUS

                role1 shouldBe role2
            }

            test("ADMIN should not equal USER") {
                Role.ADMIN shouldNotBe Role.USER
            }

            test("ADMIN should not equal ANONYMOUS") {
                Role.ADMIN shouldNotBe Role.ANONYMOUS
            }

            test("USER should not equal ANONYMOUS") {
                Role.USER shouldNotBe Role.ANONYMOUS
            }
        }

        context("Role Enum - valueOf() Method") {
            test("valueOf ADMIN should return ADMIN role") {
                val role = Role.valueOf("ADMIN")

                role shouldBe Role.ADMIN
            }

            test("valueOf USER should return USER role") {
                val role = Role.valueOf("USER")

                role shouldBe Role.USER
            }

            test("valueOf ANONYMOUS should return ANONYMOUS role") {
                val role = Role.valueOf("ANONYMOUS")

                role shouldBe Role.ANONYMOUS
            }
        }

        context("Role Enum - String Conversion Use Cases") {
            test("should convert ADMIN to string for comparison") {
                val roleString = Role.ADMIN.toString()
                val comparison = "ADMIN"

                roleString shouldBe comparison
            }

            test("should convert USER to string for comparison") {
                val roleString = Role.USER.toString()
                val comparison = "USER"

                roleString shouldBe comparison
            }

            test("should convert ANONYMOUS to string for comparison") {
                val roleString = Role.ANONYMOUS.toString()
                val comparison = "ANONYMOUS"

                roleString shouldBe comparison
            }

            test("should use in when statement with ADMIN") {
                val role = Role.ADMIN
                val result =
                    when (role) {
                        Role.ADMIN -> "Is Admin"
                        Role.USER -> "Is User"
                        Role.ANONYMOUS -> "Is Anonymous"
                    }

                result shouldBe "Is Admin"
            }

            test("should use in when statement with USER") {
                val role = Role.USER
                val result =
                    when (role) {
                        Role.ADMIN -> "Is Admin"
                        Role.USER -> "Is User"
                        Role.ANONYMOUS -> "Is Anonymous"
                    }

                result shouldBe "Is User"
            }

            test("should use in when statement with ANONYMOUS") {
                val role = Role.ANONYMOUS
                val result =
                    when (role) {
                        Role.ADMIN -> "Is Admin"
                        Role.USER -> "Is User"
                        Role.ANONYMOUS -> "Is Anonymous"
                    }

                result shouldBe "Is Anonymous"
            }
        }

        context("Role Enum - Iteration") {
            test("should iterate over all roles") {
                val roleNames = mutableListOf<String>()

                for (role in Role.entries) {
                    roleNames.add(role.toString())
                }

                roleNames shouldContain "ADMIN"
                roleNames shouldContain "USER"
                roleNames shouldContain "ANONYMOUS"
                roleNames shouldHaveSize 3
            }

            test("should map roles to strings") {
                val roleStrings = Role.entries.map { it.toString() }

                roleStrings shouldContain "ADMIN"
                roleStrings shouldContain "USER"
                roleStrings shouldContain "ANONYMOUS"
            }

            test("should filter roles") {
                val adminRoles = Role.entries.filter { it == Role.ADMIN }

                adminRoles shouldHaveSize 1
                adminRoles[0] shouldBe Role.ADMIN
            }
        }

        context("Role Enum - Ordering") {
            test("should maintain declaration order") {
                val roles = Role.entries.toList()

                roles[0] shouldBe Role.ADMIN
                roles[1] shouldBe Role.ANONYMOUS
                roles[2] shouldBe Role.USER
            }

            test("ADMIN ordinal should be 0") {
                Role.ADMIN.ordinal shouldBe 0
            }

            test("ANONYMOUS ordinal should be 1") {
                Role.ANONYMOUS.ordinal shouldBe 1
            }

            test("USER ordinal should be 2") {
                Role.USER.ordinal shouldBe 2
            }
        }
    })
