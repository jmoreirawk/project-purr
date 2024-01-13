plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "pro.moreira.projectpurr.feature.list"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
}

dependencies {
    // Modules
    implementation(project(":data"))
    // Androidx
    // Compose
    // Dependency Injection
}