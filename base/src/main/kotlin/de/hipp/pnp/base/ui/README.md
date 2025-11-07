# Accessible UI Components Library

Pre-approved accessible UI components that meet **WCAG 2.1 Level AA** standards. These components are reviewed by Vision (Accessibility Specialist) and can be used without requiring manual accessibility review.

## ✅ Approved Components

### 1. AccessibleButton
- ✓ Proper ARIA labels and roles
- ✓ Keyboard accessible
- ✓ Screen reader compatible
- ✓ Disabled state handling

```kotlin
val button = AccessibleComponentFactory.createButton(
    label = "Submit",
    onClick = "handleSubmit()",
    testId = "submit-btn",
    ariaLabel = "Submit form"
).getOrThrow()
```

### 2. AccessibleInput
- ✓ Associated labels (for/id)
- ✓ Required field indicators
- ✓ Error messaging with aria-invalid
- ✓ Screen reader announcements

```kotlin
val input = AccessibleComponentFactory.createInput(
    id = "email",
    name = "email",
    label = "Email Address",
    testId = "email-input",
    type = AccessibleInput.InputType.EMAIL,
    required = true
).getOrThrow()
```

### 3. AccessibleModal
- ✓ Focus trap (managed by implementation)
- ✓ role="dialog" and aria-modal
- ✓ Accessible close button
- ✓ Keyboard navigation (ESC to close)

```kotlin
val modal = AccessibleComponentFactory.createModal(
    id = "confirm-modal",
    title = "Confirm Action",
    content = "Are you sure?",
    onClose = "closeModal()",
    testId = "confirm-modal"
).getOrThrow()
```

### 4. AccessibleAlert
- ✓ ARIA live regions
- ✓ Proper role (alert/status)
- ✓ Screen reader announcements
- ✓ Dismissible option

```kotlin
val alert = AccessibleAlert(
    message = "Changes saved successfully",
    type = AccessibleAlert.AlertType.SUCCESS,
    live = AccessibleAlert.LiveRegion.POLITE,
    testId = "success-alert"
)
```

### 5. AccessibleLink
- ✓ Clear link text
- ✓ New window indication
- ✓ Security (rel="noopener noreferrer")

```kotlin
val link = AccessibleLink(
    text = "Learn more",
    href = "/docs",
    ariaLabel = "Learn more about our service",
    testId = "learn-more-link"
)
```

## 🧪 Automated Testing

All components have comprehensive Kotest tests in `AccessibilityTest.kt` that verify:
- ARIA attributes
- Label associations
- Keyboard navigation
- Screen reader compatibility
- Error handling
- String edge cases (empty, whitespace, unicode, emoji)

## 🚀 Usage Benefits

Using these pre-approved components provides:

1. **Skip manual Vision review** - These components are pre-approved
2. **Guaranteed WCAG compliance** - Built-in accessibility
3. **Automated validation** - Factory methods validate on creation
4. **Fast development** - No waiting for accessibility review
5. **Consistent UX** - Standard patterns across the app

## 📋 Validation

All components have built-in validation:

```kotlin
val result = AccessibleComponentFactory.createButton(
    label = "",  // Invalid - blank label
    onClick = "test()",
    testId = "btn"
)

result.isFailure  // true
result.exceptionOrNull()?.message  // "Button validation failed: Button label cannot be blank..."
```

## 🔧 Extending

To add new pre-approved components:

1. Create component data class in `AccessibleComponents.kt`
2. Include `validate()` method
3. Add to `AccessibleComponentFactory`
4. Write comprehensive tests in `AccessibilityTest.kt`
5. **Submit for Vision review** to get pre-approval
6. Document in this README

## 📚 Resources

- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [ARIA Authoring Practices](https://www.w3.org/WAI/ARIA/apg/)
- Project accessibility standards: See `~/scrum-agents/accessibility-specialist-agent.md`
