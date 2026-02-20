import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.realm)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.gov.govuk.topics"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "TOPICS_BASE_URL", "\"https://app.integration.publishing.service.gov.uk/static/topics/\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "TOPICS_BASE_URL", "\"https://app.publishing.service.gov.uk/static/topics/\"")
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
            properties["sonar.coverage.exclusions"].toString() + ",**/TopicsRealmProvider.*"
        )
        property(
            "sonar.cpd.exclusions",
            properties["sonar.cpd.exclusions"].toString() + ",**/TopicsRealmProvider.*"
        )
    }
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.design)
    implementation(projects.feature.visited)
    implementation(projects.data)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.icons)
    implementation(libs.hilt.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.realm.base)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
