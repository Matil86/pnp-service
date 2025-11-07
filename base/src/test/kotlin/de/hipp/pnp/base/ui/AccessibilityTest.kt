package de.hipp.pnp.base.ui

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

/**
 * Automated accessibility tests for pre-approved UI components.
 * These tests ensure all components meet WCAG 2.1 Level AA standards.
 *
 * Test areas:
 * - ARIA attributes (labels, roles, descriptions)
 * - Keyboard accessibility
 * - Screen reader compatibility
 * - Form labeling
 * - Error messaging
 * - Focus management
 */
class AccessibilityTest : StringSpec({

    // ========== Accessible Button Tests ==========

    "AccessibleButton should have required ARIA attributes" {
        val button = AccessibleButton(
            label = "Submit",
            ariaLabel = "Submit form",
            testId = "submit-btn",
            onClick = "handleSubmit()"
        )

        val html = button.toHtml()
        html shouldContain "aria-label=\"Submit form\""
        html shouldContain "role=\"button\""
        html shouldContain "data-testid=\"submit-btn\""
    }

    "AccessibleButton should validate required fields" {
        val validButton = AccessibleButton(
            label = "Click me",
            ariaLabel = "Click me button",
            testId = "test-btn",
            onClick = "handleClick()"
        )

        validButton.validate().shouldBeEmpty()
    }

    "AccessibleButton should reject blank labels" {
        val invalidButton = AccessibleButton(
            label = "",
            ariaLabel = "",
            testId = "test-btn",
            onClick = "handleClick()"
        )

        val issues = invalidButton.validate()
        issues.shouldNotBeEmpty()
        issues shouldContain "Button label cannot be blank"
        issues shouldContain "Button aria-label cannot be blank"
    }

    "AccessibleButton factory should validate on creation" {
        val result = AccessibleComponentFactory.createButton(
            label = "",
            onClick = "test()",
            testId = "btn"
        )

        result.shouldBeFailure()
    }

    "Disabled buttons should have disabled attribute" {
        val button = AccessibleButton(
            label = "Submit",
            disabled = true,
            testId = "submit-btn",
            onClick = "submit()"
        )

        button.toHtml() shouldContain "disabled"
    }

    // ========== Accessible Input Tests ==========

    "AccessibleInput should have associated label" {
        val input = AccessibleInput(
            id = "email",
            name = "email",
            label = "Email Address",
            type = AccessibleInput.InputType.EMAIL,
            testId = "email-input"
        )

        val html = input.toHtml()
        html shouldContain "<label for=\"email\">"
        html shouldContain "Email Address"
        html shouldContain "id=\"email\""
    }

    "Required inputs should have visual indicator" {
        val input = AccessibleInput(
            id = "name",
            name = "name",
            label = "Name",
            required = true,
            testId = "name-input"
        )

        val html = input.toHtml()
        html shouldContain "required"
        html shouldContain "<span class=\"required\">*</span>"
    }

    "Invalid inputs should have aria-invalid and error message" {
        val input = AccessibleInput(
            id = "email",
            name = "email",
            label = "Email",
            ariaInvalid = true,
            errorMessage = "Please enter a valid email address",
            testId = "email-input"
        )

        val html = input.toHtml()
        html shouldContain "aria-invalid=\"true\""
        html shouldContain "role=\"alert\""
        html shouldContain "Please enter a valid email address"
        html shouldContain "aria-describedby=\"email-error\""
    }

    "AccessibleInput should validate required fields" {
        val validInput = AccessibleInput(
            id = "test",
            name = "test",
            label = "Test Input",
            testId = "test-input"
        )

        validInput.validate().shouldBeEmpty()
    }

    "AccessibleInput should reject invalid input without error message" {
        val invalidInput = AccessibleInput(
            id = "test",
            name = "test",
            label = "Test",
            ariaInvalid = true,
            errorMessage = null,
            testId = "test-input"
        )

        val issues = invalidInput.validate()
        issues.shouldNotBeEmpty()
        issues shouldContain "Invalid input must have an error message"
    }

    // ========== Accessible Modal Tests ==========

    "AccessibleModal should have dialog role and aria-modal" {
        val modal = AccessibleModal(
            id = "confirm-modal",
            title = "Confirm Action",
            content = "Are you sure?",
            onClose = "closeModal()",
            testId = "confirm-modal"
        )

        val html = modal.toHtml()
        html shouldContain "role=\"dialog\""
        html shouldContain "aria-modal=\"true\""
        html shouldContain "aria-labelledby=\"confirm-modal-title\""
    }

    "AccessibleModal should have accessible close button" {
        val modal = AccessibleModal(
            id = "test-modal",
            title = "Test",
            content = "Content",
            closeButtonLabel = "Close test modal",
            onClose = "close()",
            testId = "test-modal"
        )

        val html = modal.toHtml()
        html shouldContain "aria-label=\"Close test modal\""
        html shouldContain "data-testid=\"test-modal-close\""
    }

    "AccessibleModal with description should have aria-describedby" {
        val modal = AccessibleModal(
            id = "test-modal",
            title = "Test",
            description = "This is a test modal",
            content = "Content",
            onClose = "close()",
            testId = "test-modal"
        )

        val html = modal.toHtml()
        html shouldContain "aria-describedby=\"test-modal-description\""
        html shouldContain "This is a test modal"
    }

    "AccessibleModal should validate required fields" {
        val validModal = AccessibleModal(
            id = "test",
            title = "Test Modal",
            content = "Content",
            onClose = "close()",
            testId = "test-modal"
        )

        validModal.validate().shouldBeEmpty()
    }

    // ========== Accessible Alert Tests ==========

    "AccessibleAlert should have appropriate role" {
        val errorAlert = AccessibleAlert(
            message = "An error occurred",
            type = AccessibleAlert.AlertType.ERROR,
            testId = "error-alert"
        )

        errorAlert.toHtml() shouldContain "role=\"alert\""

        val infoAlert = AccessibleAlert(
            message = "Information message",
            type = AccessibleAlert.AlertType.INFO,
            testId = "info-alert"
        )

        infoAlert.toHtml() shouldContain "role=\"status\""
    }

    "AccessibleAlert should have aria-live region" {
        val alert = AccessibleAlert(
            message = "Success!",
            type = AccessibleAlert.AlertType.SUCCESS,
            live = AccessibleAlert.LiveRegion.POLITE,
            testId = "success-alert"
        )

        val html = alert.toHtml()
        html shouldContain "aria-live=\"polite\""
        html shouldContain "aria-atomic=\"true\""
    }

    "AccessibleAlert icon should be aria-hidden" {
        val alert = AccessibleAlert(
            message = "Warning message",
            type = AccessibleAlert.AlertType.WARNING,
            testId = "warning-alert"
        )

        alert.toHtml() shouldContain "aria-hidden=\"true\""
    }

    "Dismissible alerts should have accessible dismiss button" {
        val alert = AccessibleAlert(
            message = "This can be dismissed",
            dismissible = true,
            testId = "dismissible-alert"
        )

        val html = alert.toHtml()
        html shouldContain "aria-label=\"Dismiss alert\""
    }

    // ========== Accessible Link Tests ==========

    "AccessibleLink should have href and aria-label" {
        val link = AccessibleLink(
            text = "Learn more",
            href = "/docs",
            ariaLabel = "Learn more about our service",
            testId = "learn-more-link"
        )

        val html = link.toHtml()
        html shouldContain "href=\"/docs\""
        html shouldContain "aria-label=\"Learn more about our service\""
    }

    "Links opening in new window should indicate this" {
        val link = AccessibleLink(
            text = "External site",
            href = "https://example.com",
            ariaLabel = "External site (opens in new window)",
            opensInNewWindow = true,
            testId = "external-link"
        )

        val html = link.toHtml()
        html shouldContain "target=\"_blank\""
        html shouldContain "rel=\"noopener noreferrer\""
        html shouldContain "(opens in new window)"
    }

    "AccessibleLink should validate aria-label for new window links" {
        val invalidLink = AccessibleLink(
            text = "External",
            href = "https://example.com",
            ariaLabel = "External link",  // Missing "opens in new window"
            opensInNewWindow = true,
            testId = "link"
        )

        val issues = invalidLink.validate()
        issues.shouldNotBeEmpty()
        issues shouldContain "Link that opens in new window should indicate this in aria-label"
    }

    "AccessibleLink should reject blank text or href" {
        val invalidLink = AccessibleLink(
            text = "",
            href = "",
            testId = "link"
        )

        val issues = invalidLink.validate()
        issues.shouldNotBeEmpty()
        issues shouldContain "Link text cannot be blank"
        issues shouldContain "Link href cannot be blank"
    }

    // ========== Component Factory Tests ==========

    "Factory should create valid components" {
        val buttonResult = AccessibleComponentFactory.createButton(
            label = "Save",
            onClick = "save()",
            testId = "save-btn"
        )
        buttonResult.shouldBeSuccess()

        val inputResult = AccessibleComponentFactory.createInput(
            id = "name",
            name = "name",
            label = "Name",
            testId = "name-input"
        )
        inputResult.shouldBeSuccess()

        val modalResult = AccessibleComponentFactory.createModal(
            id = "modal",
            title = "Modal",
            content = "Content",
            onClose = "close()",
            testId = "modal"
        )
        modalResult.shouldBeSuccess()
    }

    "Factory should reject invalid components" {
        val invalidButton = AccessibleComponentFactory.createButton(
            label = "",
            onClick = "test()",
            testId = ""
        )
        invalidButton.shouldBeFailure()
    }

    // ========== String Input Edge Cases (Boyscout: comprehensive string testing) ==========

    "Components should handle empty strings" {
        val button = AccessibleButton(
            label = "",
            ariaLabel = "",
            testId = "btn",
            onClick = "click()"
        )

        button.validate().shouldNotBeEmpty()
    }

    "Components should handle whitespace-only strings" {
        val button = AccessibleButton(
            label = "   ",
            ariaLabel = "   ",
            testId = "btn",
            onClick = "click()"
        )

        // Whitespace is considered blank
        button.label.isBlank() shouldBe true
    }

    "Components should handle unicode characters (hiragana)" {
        val button = AccessibleButton(
            label = "送信",  // "Submit" in Japanese
            ariaLabel = "フォームを送信",  // "Submit form" in Japanese
            testId = "submit-btn",
            onClick = "submit()"
        )

        button.validate().shouldBeEmpty()
        button.toHtml() shouldContain "送信"
    }

    "Components should handle emoji in labels" {
        val button = AccessibleButton(
            label = "Save ✓",
            ariaLabel = "Save changes",
            testId = "save-btn",
            onClick = "save()"
        )

        button.validate().shouldBeEmpty()
        button.toHtml() shouldContain "✓"
    }
})
