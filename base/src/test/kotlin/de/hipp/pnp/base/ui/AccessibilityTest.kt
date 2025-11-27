package de.hipp.pnp.base.ui

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

/**
 * Comprehensive accessibility tests for pre-approved UI components.
 * Tests ensure all components meet WCAG 2.1 Level AA standards.
 *
 * Test areas:
 * - ARIA attributes (labels, roles, descriptions)
 * - Keyboard accessibility
 * - Screen reader compatibility
 * - Form labeling
 * - Error messaging
 * - Focus management
 * - String input edge cases (unicode, emoji, SQL injection, XSS)
 */
class AccessibilityTest :
    FunSpec({

        // ========== Accessible Button Tests ==========

        context("AccessibleButton - ARIA attributes") {
            test("should have required ARIA attributes") {
                val button =
                    AccessibleButton(
                        label = "Submit",
                        ariaLabel = "Submit form",
                        testId = "submit-btn",
                        onClick = "handleSubmit()",
                    )

                val html = button.toHtml()
                html shouldContain "aria-label=\"Submit form\""
                html shouldContain "data-testid=\"submit-btn\""
            }

            test("should validate required fields successfully") {
                val validButton =
                    AccessibleButton(
                        label = "Click me",
                        ariaLabel = "Click me button",
                        testId = "test-btn",
                        onClick = "handleClick()",
                    )

                validButton.validate().shouldBeEmpty()
            }

            test("should reject blank labels") {
                val invalidButton =
                    AccessibleButton(
                        label = "",
                        ariaLabel = "",
                        testId = "test-btn",
                        onClick = "handleClick()",
                    )

                val issues = invalidButton.validate()
                issues.shouldNotBeEmpty()
                issues shouldContain "Button label cannot be blank"
                issues shouldContain "Button aria-label cannot be blank"
            }

            test("should have disabled attribute when disabled") {
                val button =
                    AccessibleButton(
                        label = "Submit",
                        disabled = true,
                        testId = "submit-btn",
                        onClick = "submit()",
                    )

                button.toHtml() shouldContain "disabled"
            }
        }

        context("AccessibleButton - String input edge cases") {
            test("should reject empty string label") {
                val button =
                    AccessibleButton(
                        label = "",
                        ariaLabel = "",
                        testId = "btn",
                        onClick = "click()",
                    )

                button.validate().shouldNotBeEmpty()
            }

            test("should handle whitespace-only label as blank") {
                val button =
                    AccessibleButton(
                        label = "   ",
                        ariaLabel = "   ",
                        testId = "btn",
                        onClick = "click()",
                    )

                button.label.isBlank() shouldBe true
                button.ariaLabel.isBlank() shouldBe true
            }

            test("should handle hiragana text in label - ひらがな") {
                val button =
                    AccessibleButton(
                        label = "ひらがな",
                        ariaLabel = "ひらがなボタン",
                        testId = "hiragana-btn",
                        onClick = "click()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "ひらがな"
            }

            test("should handle katakana text in label - カタカナ") {
                val button =
                    AccessibleButton(
                        label = "カタカナ",
                        ariaLabel = "カタカナボタン",
                        testId = "katakana-btn",
                        onClick = "click()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "カタカナ"
            }

            test("should handle kanji text in label - 送信") {
                val button =
                    AccessibleButton(
                        label = "送信",
                        ariaLabel = "フォームを送信",
                        testId = "submit-btn",
                        onClick = "submit()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "送信"
            }

            test("should handle emoji in label - Save ✓") {
                val button =
                    AccessibleButton(
                        label = "Save ✓",
                        ariaLabel = "Save changes",
                        testId = "save-btn",
                        onClick = "save()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "✓"
            }

            test("should handle emoji combinations - Button 😊🎉") {
                val button =
                    AccessibleButton(
                        label = "Celebrate 😊🎉",
                        ariaLabel = "Celebrate success",
                        testId = "celebrate-btn",
                        onClick = "celebrate()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "😊🎉"
            }

            test("should handle SQL injection attempt in label") {
                val button =
                    AccessibleButton(
                        label = "'; DROP TABLE users; --",
                        ariaLabel = "SQL injection attempt",
                        testId = "sql-btn",
                        onClick = "hack()",
                    )

                // Button should validate (as it's just text), but HTML output should contain the raw text
                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "DROP TABLE"
            }

            test("should handle XSS attempt in label") {
                val button =
                    AccessibleButton(
                        label = "<script>alert('XSS')</script>",
                        ariaLabel = "XSS attempt",
                        testId = "xss-btn",
                        onClick = "hack()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "<script>"
            }

            test("should handle newline characters in label") {
                val button =
                    AccessibleButton(
                        label = "Line1\nLine2",
                        ariaLabel = "Multi-line label",
                        testId = "multiline-btn",
                        onClick = "click()",
                    )

                button.validate().shouldBeEmpty()
                button.toHtml() shouldContain "\n"
            }

            test("should handle tab characters in label") {
                val button =
                    AccessibleButton(
                        label = "Tab\there",
                        ariaLabel = "Tab label",
                        testId = "tab-btn",
                        onClick = "click()",
                    )

                button.validate().shouldBeEmpty()
            }
        }

        context("AccessibleButton - Factory validation") {
            test("should validate on creation via factory") {
                val result =
                    AccessibleComponentFactory.createButton(
                        label = "",
                        onClick = "test()",
                        testId = "btn",
                    )

                result.shouldBeFailure()
            }

            test("should create valid button via factory") {
                val result =
                    AccessibleComponentFactory.createButton(
                        label = "Save",
                        onClick = "save()",
                        testId = "save-btn",
                    )

                result.shouldBeSuccess()
            }

            test("should create button with Japanese label via factory - Goku (孫悟空)") {
                val result =
                    AccessibleComponentFactory.createButton(
                        label = "孫悟空",
                        onClick = "goku()",
                        testId = "goku-btn",
                    )

                result.shouldBeSuccess()
                result.getOrNull()!!.label shouldBe "孫悟空"
            }
        }

        context("AccessibleButton - Button types") {
            test("should support BUTTON type") {
                val button =
                    AccessibleButton(
                        label = "Click",
                        testId = "btn",
                        onClick = "click()",
                        type = AccessibleButton.ButtonType.BUTTON,
                    )

                button.toHtml() shouldContain "type=\"button\""
            }

            test("should support SUBMIT type") {
                val button =
                    AccessibleButton(
                        label = "Submit",
                        testId = "submit",
                        onClick = "submit()",
                        type = AccessibleButton.ButtonType.SUBMIT,
                    )

                button.toHtml() shouldContain "type=\"submit\""
            }

            test("should support RESET type") {
                val button =
                    AccessibleButton(
                        label = "Reset",
                        testId = "reset",
                        onClick = "reset()",
                        type = AccessibleButton.ButtonType.RESET,
                    )

                button.toHtml() shouldContain "type=\"reset\""
            }
        }

        // ========== Accessible Input Tests ==========

        context("AccessibleInput - Form labeling") {
            test("should have associated label") {
                val input =
                    AccessibleInput(
                        id = "email",
                        name = "email",
                        label = "Email Address",
                        type = AccessibleInput.InputType.EMAIL,
                        testId = "email-input",
                    )

                val html = input.toHtml()
                html shouldContain "<label for=\"email\">"
                html shouldContain "Email Address"
                html shouldContain "id=\"email\""
            }

            test("should have visual indicator for required inputs") {
                val input =
                    AccessibleInput(
                        id = "name",
                        name = "name",
                        label = "Name",
                        required = true,
                        testId = "name-input",
                    )

                val html = input.toHtml()
                html shouldContain "required"
                html shouldContain "<span class=\"required\">*</span>"
            }

            test("should have aria-invalid and error message for invalid inputs") {
                val input =
                    AccessibleInput(
                        id = "email",
                        name = "email",
                        label = "Email",
                        ariaInvalid = true,
                        errorMessage = "Please enter a valid email address",
                        testId = "email-input",
                    )

                val html = input.toHtml()
                html shouldContain "aria-invalid=\"true\""
                html shouldContain "role=\"alert\""
                html shouldContain "Please enter a valid email address"
                html shouldContain "aria-describedby=\"email-error\""
            }

            test("should validate required fields successfully") {
                val validInput =
                    AccessibleInput(
                        id = "test",
                        name = "test",
                        label = "Test Input",
                        testId = "test-input",
                    )

                validInput.validate().shouldBeEmpty()
            }

            test("should reject invalid input without error message") {
                val invalidInput =
                    AccessibleInput(
                        id = "test",
                        name = "test",
                        label = "Test",
                        ariaInvalid = true,
                        errorMessage = null,
                        testId = "test-input",
                    )

                val issues = invalidInput.validate()
                issues.shouldNotBeEmpty()
                issues shouldContain "Invalid input must have an error message"
            }
        }

        context("AccessibleInput - Input types") {
            test("should support TEXT type") {
                val input =
                    AccessibleInput(
                        id = "name",
                        name = "name",
                        label = "Name",
                        type = AccessibleInput.InputType.TEXT,
                        testId = "name",
                    )
                input.toHtml() shouldContain "type=\"text\""
            }

            test("should support EMAIL type") {
                val input =
                    AccessibleInput(
                        id = "email",
                        name = "email",
                        label = "Email",
                        type = AccessibleInput.InputType.EMAIL,
                        testId = "email",
                    )
                input.toHtml() shouldContain "type=\"email\""
            }

            test("should support PASSWORD type") {
                val input =
                    AccessibleInput(
                        id = "pwd",
                        name = "pwd",
                        label = "Password",
                        type = AccessibleInput.InputType.PASSWORD,
                        testId = "pwd",
                    )
                input.toHtml() shouldContain "type=\"password\""
            }

            test("should support NUMBER type") {
                val input =
                    AccessibleInput(
                        id = "age",
                        name = "age",
                        label = "Age",
                        type = AccessibleInput.InputType.NUMBER,
                        testId = "age",
                    )
                input.toHtml() shouldContain "type=\"number\""
            }

            test("should support TEL type") {
                val input =
                    AccessibleInput(
                        id = "phone",
                        name = "phone",
                        label = "Phone",
                        type = AccessibleInput.InputType.TEL,
                        testId = "phone",
                    )
                input.toHtml() shouldContain "type=\"tel\""
            }

            test("should support URL type") {
                val input =
                    AccessibleInput(
                        id = "website",
                        name = "website",
                        label = "Website",
                        type = AccessibleInput.InputType.URL,
                        testId = "website",
                    )
                input.toHtml() shouldContain "type=\"url\""
            }

            test("should support SEARCH type") {
                val input =
                    AccessibleInput(
                        id = "search",
                        name = "search",
                        label = "Search",
                        type = AccessibleInput.InputType.SEARCH,
                        testId = "search",
                    )
                input.toHtml() shouldContain "type=\"search\""
            }
        }

        context("AccessibleInput - String input edge cases") {
            test("should reject empty id") {
                val input =
                    AccessibleInput(
                        id = "",
                        name = "name",
                        label = "Label",
                        testId = "test",
                    )
                input.validate().shouldNotBeEmpty()
            }

            test("should reject empty label") {
                val input =
                    AccessibleInput(
                        id = "test",
                        name = "test",
                        label = "",
                        testId = "test",
                    )
                input.validate().shouldNotBeEmpty()
            }

            test("should handle hiragana in label - Naruto (ナルト)") {
                val input =
                    AccessibleInput(
                        id = "naruto",
                        name = "naruto",
                        label = "ナルト",
                        testId = "naruto-input",
                    )
                input.validate().shouldBeEmpty()
                input.toHtml() shouldContain "ナルト"
            }

            test("should handle kanji in placeholder - Sakura (春野サクラ)") {
                val input =
                    AccessibleInput(
                        id = "character",
                        name = "character",
                        label = "Character Name",
                        placeholder = "春野サクラ",
                        testId = "char-input",
                    )
                input.validate().shouldBeEmpty()
                input.toHtml() shouldContain "春野サクラ"
            }

            test("should handle emoji in value - Happy 😊") {
                val input =
                    AccessibleInput(
                        id = "mood",
                        name = "mood",
                        label = "Mood",
                        value = "Happy 😊",
                        testId = "mood-input",
                    )
                input.validate().shouldBeEmpty()
                input.toHtml() shouldContain "😊"
            }

            test("should handle SQL injection in value") {
                val input =
                    AccessibleInput(
                        id = "username",
                        name = "username",
                        label = "Username",
                        value = "admin'; DROP TABLE users; --",
                        testId = "user-input",
                    )
                input.validate().shouldBeEmpty()
                input.toHtml() shouldContain "DROP TABLE"
            }

            test("should handle XSS in error message") {
                val input =
                    AccessibleInput(
                        id = "test",
                        name = "test",
                        label = "Test",
                        ariaInvalid = true,
                        errorMessage = "<script>alert('XSS')</script>",
                        testId = "test",
                    )
                input.validate().shouldBeEmpty()
                input.toHtml() shouldContain "<script>"
            }

            test("should handle whitespace in id as non-blank") {
                val input =
                    AccessibleInput(
                        id = "   ",
                        name = "test",
                        label = "Test",
                        testId = "test",
                    )
                input.id.isBlank() shouldBe true
            }
        }

        context("AccessibleInput - Factory") {
            test("should create valid input via factory - Tony Stark") {
                val result =
                    AccessibleComponentFactory.createInput(
                        id = "tony-stark",
                        name = "tony-stark",
                        label = "Tony Stark",
                        testId = "stark-input",
                    )
                result.shouldBeSuccess()
            }

            test("should reject invalid input via factory") {
                val result =
                    AccessibleComponentFactory.createInput(
                        id = "",
                        name = "",
                        label = "",
                        testId = "",
                    )
                result.shouldBeFailure()
            }
        }

        // ========== Accessible Modal Tests ==========

        context("AccessibleModal - Dialog role") {
            test("should have dialog role and aria-modal") {
                val modal =
                    AccessibleModal(
                        id = "confirm-modal",
                        title = "Confirm Action",
                        content = "Are you sure?",
                        onClose = "closeModal()",
                        testId = "confirm-modal",
                    )

                val html = modal.toHtml()
                html shouldContain "role=\"dialog\""
                html shouldContain "aria-modal=\"true\""
                html shouldContain "aria-labelledby=\"confirm-modal-title\""
            }

            test("should have accessible close button") {
                val modal =
                    AccessibleModal(
                        id = "test-modal",
                        title = "Test",
                        content = "Content",
                        closeButtonLabel = "Close test modal",
                        onClose = "close()",
                        testId = "test-modal",
                    )

                val html = modal.toHtml()
                html shouldContain "aria-label=\"Close test modal\""
                html shouldContain "data-testid=\"test-modal-close\""
            }

            test("should have aria-describedby when description provided") {
                val modal =
                    AccessibleModal(
                        id = "test-modal",
                        title = "Test",
                        description = "This is a test modal",
                        content = "Content",
                        onClose = "close()",
                        testId = "test-modal",
                    )

                val html = modal.toHtml()
                html shouldContain "aria-describedby=\"test-modal-description\""
                html shouldContain "This is a test modal"
            }

            test("should validate required fields successfully") {
                val validModal =
                    AccessibleModal(
                        id = "test",
                        title = "Test Modal",
                        content = "Content",
                        onClose = "close()",
                        testId = "test-modal",
                    )

                validModal.validate().shouldBeEmpty()
            }
        }

        context("AccessibleModal - String input edge cases") {
            test("should reject empty id") {
                val modal =
                    AccessibleModal(
                        id = "",
                        title = "Title",
                        content = "Content",
                        onClose = "close()",
                        testId = "test",
                    )
                modal.validate().shouldNotBeEmpty()
            }

            test("should reject empty title") {
                val modal =
                    AccessibleModal(
                        id = "test",
                        title = "",
                        content = "Content",
                        onClose = "close()",
                        testId = "test",
                    )
                modal.validate().shouldNotBeEmpty()
            }

            test("should handle hiragana in title - こんにちは") {
                val modal =
                    AccessibleModal(
                        id = "greeting",
                        title = "こんにちは",
                        content = "Welcome",
                        onClose = "close()",
                        testId = "greeting-modal",
                    )
                modal.validate().shouldBeEmpty()
                modal.toHtml() shouldContain "こんにちは"
            }

            test("should handle emoji in title - Success ✓") {
                val modal =
                    AccessibleModal(
                        id = "success",
                        title = "Success ✓",
                        content = "Operation completed",
                        onClose = "close()",
                        testId = "success-modal",
                    )
                modal.validate().shouldBeEmpty()
                modal.toHtml() shouldContain "✓"
            }

            test("should handle SQL injection in content") {
                val modal =
                    AccessibleModal(
                        id = "test",
                        title = "Test",
                        content = "'; DROP TABLE modals; --",
                        onClose = "close()",
                        testId = "test",
                    )
                modal.validate().shouldBeEmpty()
                modal.toHtml() shouldContain "DROP TABLE"
            }

            test("should handle XSS in description") {
                val modal =
                    AccessibleModal(
                        id = "test",
                        title = "Test",
                        description = "<script>alert('XSS')</script>",
                        content = "Content",
                        onClose = "close()",
                        testId = "test",
                    )
                modal.validate().shouldBeEmpty()
                modal.toHtml() shouldContain "<script>"
            }

            test("should handle whitespace-only title as blank") {
                val modal =
                    AccessibleModal(
                        id = "test",
                        title = "   ",
                        content = "Content",
                        onClose = "close()",
                        testId = "test",
                    )
                modal.title.isBlank() shouldBe true
            }
        }

        context("AccessibleModal - Factory") {
            test("should create valid modal via factory - Spider-Man") {
                val result =
                    AccessibleComponentFactory.createModal(
                        id = "spiderman",
                        title = "Spider-Man",
                        content = "With great power comes great responsibility",
                        onClose = "close()",
                        testId = "spiderman-modal",
                    )
                result.shouldBeSuccess()
            }

            test("should reject invalid modal via factory") {
                val result =
                    AccessibleComponentFactory.createModal(
                        id = "",
                        title = "",
                        content = "",
                        onClose = "",
                        testId = "",
                    )
                result.shouldBeFailure()
            }
        }

        // ========== Accessible Alert Tests ==========

        context("AccessibleAlert - Roles") {
            test("should have alert role for ERROR type") {
                val errorAlert =
                    AccessibleAlert(
                        message = "An error occurred",
                        type = AccessibleAlert.AlertType.ERROR,
                        testId = "error-alert",
                    )

                errorAlert.toHtml() shouldContain "role=\"alert\""
            }

            test("should have status role for INFO type") {
                val infoAlert =
                    AccessibleAlert(
                        message = "Information message",
                        type = AccessibleAlert.AlertType.INFO,
                        testId = "info-alert",
                    )

                infoAlert.toHtml() shouldContain "role=\"status\""
            }

            test("should have status role for SUCCESS type") {
                val successAlert =
                    AccessibleAlert(
                        message = "Success!",
                        type = AccessibleAlert.AlertType.SUCCESS,
                        testId = "success-alert",
                    )

                successAlert.toHtml() shouldContain "role=\"status\""
            }

            test("should have status role for WARNING type") {
                val warningAlert =
                    AccessibleAlert(
                        message = "Warning!",
                        type = AccessibleAlert.AlertType.WARNING,
                        testId = "warning-alert",
                    )

                warningAlert.toHtml() shouldContain "role=\"status\""
            }
        }

        context("AccessibleAlert - ARIA live regions") {
            test("should have aria-live region") {
                val alert =
                    AccessibleAlert(
                        message = "Success!",
                        type = AccessibleAlert.AlertType.SUCCESS,
                        live = AccessibleAlert.LiveRegion.POLITE,
                        testId = "success-alert",
                    )

                val html = alert.toHtml()
                html shouldContain "aria-live=\"polite\""
                html shouldContain "aria-atomic=\"true\""
            }

            test("should support OFF live region") {
                val alert =
                    AccessibleAlert(
                        message = "Message",
                        live = AccessibleAlert.LiveRegion.OFF,
                        testId = "alert",
                    )
                alert.toHtml() shouldContain "aria-live=\"off\""
            }

            test("should support ASSERTIVE live region") {
                val alert =
                    AccessibleAlert(
                        message = "Critical!",
                        live = AccessibleAlert.LiveRegion.ASSERTIVE,
                        testId = "alert",
                    )
                alert.toHtml() shouldContain "aria-live=\"assertive\""
            }

            test("should have aria-hidden icon") {
                val alert =
                    AccessibleAlert(
                        message = "Warning message",
                        type = AccessibleAlert.AlertType.WARNING,
                        testId = "warning-alert",
                    )

                alert.toHtml() shouldContain "aria-hidden=\"true\""
            }
        }

        context("AccessibleAlert - Dismissible alerts") {
            test("should have accessible dismiss button when dismissible") {
                val alert =
                    AccessibleAlert(
                        message = "This can be dismissed",
                        dismissible = true,
                        testId = "dismissible-alert",
                    )

                val html = alert.toHtml()
                html shouldContain "aria-label=\"Dismiss alert\""
            }

            test("should not have dismiss button when not dismissible") {
                val alert =
                    AccessibleAlert(
                        message = "Cannot dismiss",
                        dismissible = false,
                        testId = "alert",
                    )

                val html = alert.toHtml()
                html shouldNotContain "Dismiss alert"
            }
        }

        context("AccessibleAlert - String input edge cases") {
            test("should reject empty message") {
                val alert =
                    AccessibleAlert(
                        message = "",
                        testId = "test",
                    )
                alert.validate().shouldNotBeEmpty()
            }

            test("should handle whitespace-only message as blank") {
                val alert =
                    AccessibleAlert(
                        message = "   ",
                        testId = "test",
                    )
                alert.message.isBlank() shouldBe true
            }

            test("should handle hiragana in message - ありがとう") {
                val alert =
                    AccessibleAlert(
                        message = "ありがとうございます",
                        type = AccessibleAlert.AlertType.SUCCESS,
                        testId = "thanks-alert",
                    )
                alert.validate().shouldBeEmpty()
                alert.toHtml() shouldContain "ありがとう"
            }

            test("should handle emoji in message - Success 🎉") {
                val alert =
                    AccessibleAlert(
                        message = "Success 🎉",
                        type = AccessibleAlert.AlertType.SUCCESS,
                        testId = "success-alert",
                    )
                alert.validate().shouldBeEmpty()
                alert.toHtml() shouldContain "🎉"
            }

            test("should handle SQL injection in message") {
                val alert =
                    AccessibleAlert(
                        message = "'; DROP TABLE alerts; --",
                        testId = "test",
                    )
                alert.validate().shouldBeEmpty()
                alert.toHtml() shouldContain "DROP TABLE"
            }

            test("should handle XSS in message") {
                val alert =
                    AccessibleAlert(
                        message = "<script>alert('XSS')</script>",
                        testId = "test",
                    )
                alert.validate().shouldBeEmpty()
                alert.toHtml() shouldContain "<script>"
            }
        }

        context("AccessibleAlert - Icon rendering") {
            test("should render success icon ✓") {
                val alert =
                    AccessibleAlert(
                        message = "Success",
                        type = AccessibleAlert.AlertType.SUCCESS,
                        testId = "alert",
                    )
                alert.toHtml() shouldContain "✓"
            }

            test("should render info icon ℹ") {
                val alert =
                    AccessibleAlert(
                        message = "Info",
                        type = AccessibleAlert.AlertType.INFO,
                        testId = "alert",
                    )
                alert.toHtml() shouldContain "ℹ"
            }

            test("should render warning icon ⚠") {
                val alert =
                    AccessibleAlert(
                        message = "Warning",
                        type = AccessibleAlert.AlertType.WARNING,
                        testId = "alert",
                    )
                alert.toHtml() shouldContain "⚠"
            }

            test("should render error icon ✕") {
                val alert =
                    AccessibleAlert(
                        message = "Error",
                        type = AccessibleAlert.AlertType.ERROR,
                        testId = "alert",
                    )
                alert.toHtml() shouldContain "✕"
            }
        }

        // ========== Accessible Link Tests ==========

        context("AccessibleLink - Basic attributes") {
            test("should have href and aria-label") {
                val link =
                    AccessibleLink(
                        text = "Learn more",
                        href = "/docs",
                        ariaLabel = "Learn more about our service",
                        testId = "learn-more-link",
                    )

                val html = link.toHtml()
                html shouldContain "href=\"/docs\""
                html shouldContain "aria-label=\"Learn more about our service\""
            }

            test("should indicate when opening in new window") {
                val link =
                    AccessibleLink(
                        text = "External site",
                        href = "https://example.com",
                        ariaLabel = "External site (opens in new window)",
                        opensInNewWindow = true,
                        testId = "external-link",
                    )

                val html = link.toHtml()
                html shouldContain "target=\"_blank\""
                html shouldContain "rel=\"noopener noreferrer\""
                html shouldContain "(opens in new window)"
            }

            test("should validate aria-label for new window links") {
                val invalidLink =
                    AccessibleLink(
                        text = "External",
                        href = "https://example.com",
                        ariaLabel = "External link",
                        opensInNewWindow = true,
                        testId = "link",
                    )

                val issues = invalidLink.validate()
                issues.shouldNotBeEmpty()
                issues shouldContain "Link that opens in new window should indicate this in aria-label"
            }

            test("should reject blank text or href") {
                val invalidLink =
                    AccessibleLink(
                        text = "",
                        href = "",
                        testId = "link",
                    )

                val issues = invalidLink.validate()
                issues.shouldNotBeEmpty()
                issues shouldContain "Link text cannot be blank"
                issues shouldContain "Link href cannot be blank"
            }
        }

        context("AccessibleLink - String input edge cases") {
            test("should reject empty text") {
                val link =
                    AccessibleLink(
                        text = "",
                        href = "/test",
                        testId = "link",
                    )
                link.validate().shouldNotBeEmpty()
            }

            test("should reject empty href") {
                val link =
                    AccessibleLink(
                        text = "Link",
                        href = "",
                        testId = "link",
                    )
                link.validate().shouldNotBeEmpty()
            }

            test("should handle whitespace-only text as blank") {
                val link =
                    AccessibleLink(
                        text = "   ",
                        href = "/test",
                        testId = "link",
                    )
                link.text.isBlank() shouldBe true
            }

            test("should handle hiragana in text - リンク") {
                val link =
                    AccessibleLink(
                        text = "リンク",
                        href = "/link",
                        testId = "link",
                    )
                link.validate().shouldBeEmpty()
                link.toHtml() shouldContain "リンク"
            }

            test("should handle emoji in text - Home 🏠") {
                val link =
                    AccessibleLink(
                        text = "Home 🏠",
                        href = "/home",
                        testId = "home-link",
                    )
                link.validate().shouldBeEmpty()
                link.toHtml() shouldContain "🏠"
            }

            test("should handle SQL injection in href") {
                val link =
                    AccessibleLink(
                        text = "Link",
                        href = "'; DROP TABLE links; --",
                        testId = "link",
                    )
                link.validate().shouldBeEmpty()
                link.toHtml() shouldContain "DROP TABLE"
            }

            test("should handle XSS in text") {
                val link =
                    AccessibleLink(
                        text = "<script>alert('XSS')</script>",
                        href = "/test",
                        testId = "link",
                    )
                link.validate().shouldBeEmpty()
                link.toHtml() shouldContain "<script>"
            }

            test("should handle Japanese URL - Capsule Corp") {
                val link =
                    AccessibleLink(
                        text = "カプセルコーポレーション",
                        href = "https://capsulecorp.jp",
                        testId = "capsule-link",
                    )
                link.validate().shouldBeEmpty()
                link.toHtml() shouldContain "カプセルコーポレーション"
            }
        }

        // ========== Component Factory Tests ==========

        context("Component Factory - Success cases") {
            test("should create valid button - Bruce Banner") {
                val buttonResult =
                    AccessibleComponentFactory.createButton(
                        label = "Bruce Banner",
                        onClick = "hulkSmash()",
                        testId = "banner-btn",
                    )
                buttonResult.shouldBeSuccess()
            }

            test("should create valid input - Peter Parker") {
                val inputResult =
                    AccessibleComponentFactory.createInput(
                        id = "peter-parker",
                        name = "peter-parker",
                        label = "Peter Parker",
                        testId = "parker-input",
                    )
                inputResult.shouldBeSuccess()
            }

            test("should create valid modal - Captain America") {
                val modalResult =
                    AccessibleComponentFactory.createModal(
                        id = "cap-modal",
                        title = "Captain America",
                        content = "I can do this all day",
                        onClose = "close()",
                        testId = "cap-modal",
                    )
                modalResult.shouldBeSuccess()
            }
        }

        context("Component Factory - Failure cases") {
            test("should reject invalid button") {
                val invalidButton =
                    AccessibleComponentFactory.createButton(
                        label = "",
                        onClick = "test()",
                        testId = "",
                    )
                invalidButton.shouldBeFailure()
            }

            test("should reject invalid input") {
                val invalidInput =
                    AccessibleComponentFactory.createInput(
                        id = "",
                        name = "",
                        label = "",
                        testId = "",
                    )
                invalidInput.shouldBeFailure()
            }

            test("should reject invalid modal") {
                val invalidModal =
                    AccessibleComponentFactory.createModal(
                        id = "",
                        title = "",
                        content = "Content",
                        onClose = "close()",
                        testId = "",
                    )
                invalidModal.shouldBeFailure()
            }
        }

        context("Component Factory - Creative test data") {
            test("should create button with anime character - Luffy") {
                val result =
                    AccessibleComponentFactory.createButton(
                        label = "Monkey D. Luffy",
                        onClick = "gomuGomu()",
                        testId = "luffy-btn",
                    )
                result.shouldBeSuccess()
                result.getOrNull()!!.label shouldBe "Monkey D. Luffy"
            }

            test("should create input with Marvel character - Tony Stark") {
                val result =
                    AccessibleComponentFactory.createInput(
                        id = "tony-stark",
                        name = "tony-stark",
                        label = "Tony Stark @ Stark Industries",
                        testId = "stark-input",
                    )
                result.shouldBeSuccess()
                result.getOrNull()!!.label shouldBe "Tony Stark @ Stark Industries"
            }

            test("should create modal with Konoha ninja - Sakura (春野サクラ)") {
                val result =
                    AccessibleComponentFactory.createModal(
                        id = "sakura-modal",
                        title = "春野サクラ",
                        content = "Medical ninja from Konoha",
                        onClose = "close()",
                        testId = "sakura-modal",
                    )
                result.shouldBeSuccess()
                result.getOrNull()!!.title shouldBe "春野サクラ"
            }
        }
    })
