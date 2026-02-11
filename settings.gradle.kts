import java.io.FileInputStream
import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.github.com/govuk-one-login/mobile-android-authentication") {
            if (file("${rootProject.projectDir.path}/github.properties").exists()) {
                val propsFile = File("${rootProject.projectDir.path}/github.properties")
                val props = Properties().also { it.load(FileInputStream(propsFile)) }
                val ghUsername = props["ghUsername"] as String?
                val ghToken = props["ghToken"] as String?

                credentials {
                    username = ghUsername
                    password = ghToken
                }
            } else {
                credentials {
                    username = System.getenv("CI_ACCESS_USERNAME")
                    password = System.getenv("CI_ACCESS_TOKEN")
                }
            }
        }
        maven("https://jitpack.io")
    }
}

rootProject.name = "govuk-mobile-android-app"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":analytics")
include(":app")
include(":config")
include(":design")
include(":feature:chat")
include(":feature:home")
include(":feature:local")
include(":feature:search")
include(":feature:settings")
include(":feature:topics")
include(":feature:visited")
include(":notifications")
include(":data")
