import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.compose.screenshot)
}

android {
    namespace = "uk.gov.govuk.settings"
    compileSdk = Version.COMPILE_SDK

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "ACCOUNT_EVENT", "\"ManageOneLoginAccount\"")
        buildConfigField("String", "ACCOUNT_URL", "\"https://home.account.gov.uk/\"")

        buildConfigField("String", "SIGN_OUT_EVENT", "\"SignOut\"")

        buildConfigField("String", "ACCESSIBILITY_STATEMENT_EVENT", "\"AccessibilityStatement\"")
        buildConfigField("String", "ACCESSIBILITY_STATEMENT_URL", "\"https://www.gov.uk/government/publications/accessibility-statement-for-the-govuk-app\"")

        buildConfigField("String", "HELP_AND_FEEDBACK_EVENT", "\"HelpAndFeedback\"")
        buildConfigField("String", "HELP_AND_FEEDBACK_URL", "\"https://www.gov.uk/contact/govuk-app\"")

        buildConfigField("String", "OPEN_SOURCE_LICENCE_EVENT", "\"OpenSourceLicenses\"")

        buildConfigField("String", "NOTIFICATIONS_PERMISSION_EVENT", "\"NotificationsPermission\"")

        buildConfigField("String", "PRIVACY_POLICY_EVENT", "\"PrivacyPolicy\"")
        buildConfigField("String", "PRIVACY_POLICY_URL", "\"https://www.gov.uk/government/publications/govuk-app-privacy-notice-how-we-use-your-data\"")

        buildConfigField("String", "TERMS_AND_CONDITIONS_EVENT", "\"TermsAndConditions\"")
        buildConfigField("String", "TERMS_AND_CONDITIONS_URL", "\"http://www.gov.uk/government/publications/govuk-app-terms-and-conditions\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.design)
    implementation(projects.config)
    implementation(projects.notifications)
    implementation(projects.data)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.hilt.android)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.play.services.measurement.api)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.ui.tooling)
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
            // force same kotlin version for dependencies to avoid version mismatch
            useVersion(libs.versions.kotlin.get())
        }
    }
}
