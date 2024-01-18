plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "pro.moreira.projectpurr.common.test"
}

dependencies {
    // Modules
    implementation(project(":data"))
    // Tests
    implementation(libs.junit)
    implementation(libs.coroutines.test)
    implementation(libs.faker)
}
