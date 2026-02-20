import com.google.android.gms.oss.licenses.plugin.LicensesTask
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.oss.licenses)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.play.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebaseAppDistribution)
    alias(libs.plugins.kover)
}

val majorVersion = "1"
val minorVersion = "1"
val patchVersion = "2"

android {
    namespace = "uk.gov.govuk"
    compileSdk = Version.COMPILE_SDK

    val buildNumber = System.getenv("VERSION_CODE")?.toInt()
        ?: System.getenv("GITHUB_RUN_NUMBER")?.toInt() ?: 1

    defaultConfig {
        applicationId = "uk.gov.govuk"
        minSdk = Version.MIN_SDK
        targetSdk = Version.TARGET_SDK
        versionCode = buildNumber
        versionName = "$majorVersion.$minorVersion.$patchVersion"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
        
        buildConfigField("String", "PLAY_STORE_URL", "\"https://play.google.com/store/apps/details?id=$applicationId\"")
        buildConfigField("String", "ONE_SIGNAL_APP_ID", "\"4c235189-5c5f-4a71-8385-2549fc36419f\"")
        buildConfigField("String", "VERSION_NAME_USER_FACING", "\"$versionName ($versionCode)\"")
    }

    signingConfigs {
        create("alpha") {
            storeFile = file("${project.rootDir}/alpha.jks")
            storePassword = System.getenv("ALPHA_KEY_PASSWORD")
            keyAlias = System.getenv("ALPHA_KEY_ALIAS")
            keyPassword = System.getenv("ALPHA_KEY_PASSWORD")
        }

        create("release") {
            storeFile = file("${project.rootDir}/release.jks")
            storePassword = System.getenv("RELEASE_KEY_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        create("alpha") {
            applicationIdSuffix = ".dev"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            matchingFallbacks += listOf("debug")

            signingConfig = if (System.getenv("ALPHA_KEYSTORE") != null) {
                signingConfigs.getByName("alpha")
            } else {
                signingConfigs.getByName("debug")
            }

            firebaseAppDistribution {
                artifactType = "APK"
                releaseNotesFile = "${project.rootDir}/releasenotes.txt"
                groups = "android-alpha-testers"
            }
        }

        debug {
            applicationIdSuffix = ".dev"
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = if (System.getenv("RELEASE_KEYSTORE") != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }

            ndk {
                debugSymbolLevel = "FULL"
            }

            buildConfigField("String", "ONE_SIGNAL_APP_ID", "\"bbea84fc-28cc-4712-a6c5-88f5d08b0d0d\"")
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

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.config)
    implementation(projects.design)
    implementation(projects.data)
    implementation(projects.feature.chat)
    implementation(projects.feature.home)
    implementation(projects.feature.local)
    implementation(projects.feature.settings)
    implementation(projects.feature.search)
    implementation(projects.feature.topics)
    implementation(projects.feature.visited)
    implementation(projects.notifications)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.browser)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.appcheck.play)

    implementation(libs.google.accompanist)

    implementation(libs.lottie.compose)
    implementation(libs.play.services.oss.licenses)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.scalars)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android)

    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}

// fixes for OSS Licenses plugin (v0.10.10) causing intermittent build crashes
// and blank license screens on recent agp versions
androidComponents.onVariants { variant ->
    val taskName = "${variant.name}OssLicensesTask"
    if (!tasks.names.contains(taskName)) return@onVariants
    val ossTask = tasks.named<LicensesTask>(taskName)

    variant.sources.res?.addGeneratedSourceDirectory(ossTask, LicensesTask::getGeneratedDirectory)

    tasks.matching { it.name.contains(variant.name, ignoreCase = true) }.configureEach {
        if (name.contains("OssLicensesCleanUp")) {
            enabled = false
        } else if (name.contains("Bundle") || name.contains("Package") || name.contains("Merge")) {
            mustRunAfter(ossTask)
        }
    }
}