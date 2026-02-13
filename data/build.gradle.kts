import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.realm)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.gov.govuk.data"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "AUTH_BASE_URL", "\"https://govukapp-staging.auth.eu-west-2.amazoncognito.com/oauth2/\"")
        buildConfigField("String", "TOKEN_BASE_URL", "\"https://4y369hyvja.execute-api.eu-west-2.amazonaws.com/staging/oauth2/\"")
        buildConfigField("String", "AUTH_CLIENT_ID", "\"7r88bunbmdsvihr2cdb43lasun\"")
        buildConfigField("String", "AUTHORIZE_ENDPOINT", "\"authorize\"")
        buildConfigField("String", "TOKEN_ENDPOINT", "\"token\"")
        buildConfigField("String", "AUTH_REDIRECT", "\"govuk://govuk/login-auth-callback\"")
        buildConfigField("String", "USER_API_BASE_URL", "\"https://d2nndycdmjjz2u.cloudfront.net\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "AUTH_BASE_URL", "\"https://govukapp.auth.eu-west-2.amazoncognito.com/oauth2/\"")
            buildConfigField("String", "TOKEN_BASE_URL", "\"https://em5hkfc46k.execute-api.eu-west-2.amazonaws.com/production/oauth2/\"")
            buildConfigField("String", "AUTH_CLIENT_ID", "\"40grlhknon5palderi2j1a1bte\"")
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
            properties["sonar.coverage.exclusions"].toString() + ",**/RealmEncryptionHelper.*,**/RealmProvider.*,**/RealmDataStore.*,**/TinkClient.*"
        )
        property(
            "sonar.cpd.exclusions",
            properties["sonar.cpd.exclusions"].toString() + ",**/RealmEncryptionHelper.*,**/RealmProvider.*,**/RealmDataStore.*,**/TinkClient.*"
        )
    }
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.appcheck.play)
    implementation(libs.androidx.browser)
    implementation(libs.google.crypto.tink)

    ksp(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.realm.base)
    implementation(libs.openid)

    implementation(libs.gov.securestore) {
        artifact {
            classifier = "release"
            type = "aar"
        }
    }

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
