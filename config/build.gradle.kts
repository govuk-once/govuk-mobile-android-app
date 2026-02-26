import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "uk.gov.govuk.config"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "CONFIG_BASE_URL", "\"https://app.integration.publishing.service.gov.uk/config/\"")
        buildConfigField("String", "CONFIG_PUBLIC_KEY", "\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEI9ifhn/iLdu3PwCKMhzqICSNUTivwF78Z9ybmhyIDF1Nvv+BavPyvz1XICfgEQ8g6IvHapaALXHcTszv5tFFfg==\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "CONFIG_BASE_URL", "\"https://app.publishing.service.gov.uk/config/\"")
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
}

sonar {
    properties {
        property(
            "sonar.coverage.exclusions",
            properties["sonar.coverage.exclusions"].toString() + ",**/DebugFlags.*"
        )
        property(
            "sonar.cpd.exclusions",
            properties["sonar.cpd.exclusions"].toString() + ",**/DebugFlags.*"
        )
    }
}

dependencies {
    implementation(projects.data)

    implementation(libs.hilt.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.scalars)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.remote.config)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
