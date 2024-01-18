plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
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
    implementation(project(":common:ui:assets"))
    // Androidx
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime.compose)
    // Compose
    with(libs.compose) {
        implementation(platform(bom))
        implementation(ui)
        implementation(material)
        implementation(tooling)
        implementation(preview)
        implementation(paging)
    }
    // Dependency Injection
    with(libs.hilt) {
        implementation(android)
        ksp(compiler)
        implementation(navigation.compose)
    }
}