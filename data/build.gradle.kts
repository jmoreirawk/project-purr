import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    namespace = "pro.moreira.projectpurr.data"

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val apiKey = properties.getProperty("CATS_API_KEY")
        buildConfigField("String", "CATS_API_URL", "\"https://api.thecatapi.com/v1/\"")
        buildConfigField("String", "CATS_API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Kotlin
    implementation(libs.coroutines)
    // Dependency Injection
    with(libs.hilt) {
        implementation(android)
        ksp(compiler)
    }
    // Data Persistence
    with(libs.room) {
        implementation(runtime)
        ksp(compiler)
        implementation(ktx)
        implementation(paging)
    }
    // Networking
    with(libs.retrofit) {
        implementation(this)
        implementation(moshi)
    }
    implementation(libs.okhttp3)
    // Serialization
    with(libs.moshi) {
        implementation(this)
        implementation(kotlin)
        ksp(kotlin.codegen)
    }
    // Testing
    testImplementation(project(":common:test"))
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.faker)
}