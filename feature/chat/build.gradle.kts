import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.realm)
    alias(libs.plugins.kover)
    alias(libs.plugins.screenshot)
}

val privacyPolicyUrl: String by project

android {
    namespace = "uk.gov.govuk.chat"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "CHAT_BASE_URL", "\"https://t4l83ued82.execute-api.eu-west-2.amazonaws.com/staging/\"")
        buildConfigField("String", "PRIVACY_POLICY_URL", privacyPolicyUrl)
        buildConfigField("Boolean", "ANIMATED_FEEDBACK_ENABLED", "true")
    }

    buildTypes {
        release {
            buildConfigField("String", "CHAT_BASE_URL", "\"https://9olgmwodf3.execute-api.eu-west-2.amazonaws.com/production/\"")
            buildConfigField("Boolean", "ANIMATED_FEEDBACK_ENABLED", "false")
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
        buildConfig = true
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.design)
    implementation(projects.data)
    implementation(projects.config)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.hilt.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.realm.base)

    implementation(libs.androidx.compose.animation)

    implementation(libs.lottie.compose)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(kotlin("test"))

    screenshotTestImplementation(libs.screenshot.validation.api) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)
}
