# Screenshot Testing Guide

This guide explains how to work with screenshot tests in this project, including what to do when tests fail or need updating.

## Overview

Screenshot tests in this project use the [Android `PreviewTest` framework](https://developer.android.com/studio/preview/compose-screenshot-testing) to capture and validate UI rendering across different configurations (light/dark modes, device sizes, etc.). They are located in the `screenshotTest` source set.

## Key Requirements

- **@PreviewLightDark annotation**: All screenshot tests MUST include the `@PreviewLightDark` annotation to ensure both light and dark mode screenshots are captured. This is critical for accessibility and design consistency.

## Running Screenshot Tests

### Validate screenshot tests (run tests without updating baselines)
```bash
./gradlew validateDebugScreenshotTest
```

### Update baseline images (after intentional changes)
```bash
./gradlew updateDebugScreenshotTest
```

Both commands will run tests for all modules in the project.

If you only want to run tests for a specific module, use the module path prefix:
# Validate tests for a specific module

```bash                                                                                         
./gradlew :app:validateDebugScreenshotTest                                                                                         
./gradlew :design:validateDebugScreenshotTest                                                                                         
./gradlew :feature:home:validateDebugScreenshotTest                                                                                         
```

# Update baselines for a specific module
```bash
./gradlew :app:updateDebugScreenshotTest                                                                                         
./gradlew :design:updateDebugScreenshotTest                                                                                         
./gradlew :feature:home:updateDebugScreenshotTest                                                                                         
```
                                                                                         
Replace `:app:`, `:design:`, `:feature:home:`, etc. with the module you're working on. This is useful when you're only modifying components in a specific module and want faster test execution.

## When Screenshot Tests Fail

### Understanding the Failure

When a screenshot test fails, it means the current UI rendering doesn't match the baseline image. This can happen for several reasons:

1. **Unintended visual changes** - A code change inadvertently affected the UI
2. **Intentional design updates** - You deliberately changed the UI design
3. **Environment differences** - Different system fonts, density settings, or rendering issues
4. **Platform/dependency updates** - Android framework, Compose, or library version changes

### Investigating the Failure

1. **Check the test output** - Look at the gradle build logs for which specific tests failed
2. **Review your changes** - Check if you made any UI-related changes in your PR
3. **Examine the diff** - Most IDEs can show you a visual diff of the baseline vs. actual screenshots

### Platform Differences

**Note**: Screenshot images generated on macOS will look different from those generated on Ubuntu in the CI pipeline due to system font rendering differences. If baseline images are captured locally on macOS but tests run on Ubuntu in CI, the images may appear different even without code changes. In such cases, the baselines should be updated to match the CI platform (Ubuntu).

## Adding New Screenshot Tests

When adding a new UI component or screen:

1. Create a screenshot test file in the appropriate `screenshotTest` source set
2. **Always include the `@PreviewLightDark` annotation** on your test method
3. Example structure:
   ```kotlin
   @PreviewTest
   @PreviewLightDark
   @Composable
   fun MyComponentScreenshots() {
       MyComponentPreview()
   }
   ```
4. Run the tests to generate the initial baseline images:
   ```bash
   ./gradlew updateDebugScreenshotTest
   ```
5. Commit both the test code and the baseline images

