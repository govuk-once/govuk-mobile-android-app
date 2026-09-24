import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

val githubProperties = Properties()
val githubPropertiesFile = rootProject.file("github.properties")
if (githubPropertiesFile.exists()) {
    githubPropertiesFile.reader().use { githubProperties.load(it) }
}

android {
    namespace = "uk.gov.govuk.analytics"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "QUALTRICS_BRAND_ID", "\"${githubProperties.getProperty("QUALTRICS_BRAND_ID") ?: System.getenv("QUALTRICS_BRAND_ID_DEFAULT") ?: ""}\"")
        buildConfigField("String", "QUALTRICS_PROJECT_ID", "\"${githubProperties.getProperty("QUALTRICS_PROJECT_ID") ?: System.getenv("QUALTRICS_PROJECT_ID_DEFAULT") ?: ""}\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "QUALTRICS_BRAND_ID", "\"${System.getenv("QUALTRICS_BRAND_ID_RELEASE") ?: ""}\"")
            buildConfigField("String", "QUALTRICS_PROJECT_ID", "\"${System.getenv("QUALTRICS_PROJECT_ID_RELEASE") ?: ""}\"")
        }
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

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

sonar {
    // Exclude firebase analytics client as it uses bundles, which are troublesome to unit test
    properties {
        property(
            "sonar.coverage.exclusions",
            properties["sonar.coverage.exclusions"].toString() + ",**/FirebaseAnalyticsClient.*"
        )
    }
}

dependencies {
    implementation(projects.design)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.hilt.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.google.tag.manager)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.qualtrics.digital.sdk)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
