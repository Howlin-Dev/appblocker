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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    id("com.google.gms.google-services") version "4.3.15" apply false
}

rootProject.name = "App Blocker"
include(":app")
include(":profiles")
include(":core")
include(":permissions")
include(":timer")
include(":settings")
