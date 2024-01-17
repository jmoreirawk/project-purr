@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
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

    versionCatalogs {
        create("libs") {
            from(files("./gradle/dependencies.versions.toml"))
        }
    }
}

rootProject.name = "Project Purr"
include(":app")
include(":data")
// Features
include(":feature:list")
include(":feature:details")
// Common
include(":common:ui:assets")
include(":common:test")
