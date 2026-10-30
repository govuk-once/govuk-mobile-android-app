import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.kover)
    alias(libs.plugins.screenshot)
}

val privacyPolicyUrl: String by project

android {
    namespace = "uk.gov.govuk.design"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PRIVACY_POLICY_URL", privacyPolicyUrl)
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

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

// Set the property for this module specifically during evaluation
//ext.set("android.compose.screenshot.maxHeapSize", "4g")

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.icons)
    implementation(libs.commonmark)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.test.manifest)

    screenshotTestImplementation(libs.screenshot.validation.api) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)
}

// Split up screenshot generation to clear the plugin's image pool cache
tasks.configureEach {
    if (name.contains("ScreenshotTest", ignoreCase = true)) {
        // Enforce zero parallel threads within the leaked task block
        //maxWorkers.set(1)

        // Force the execution engine to completely dump its cached instances frequently
        System.setProperty("android.experimental.screenshotTest.worker.jvmArgs", "-Xmx3g -XX:+UseG1GC -XX:SoftRefLRUPolicyMSPerMB=50")
    }
}
