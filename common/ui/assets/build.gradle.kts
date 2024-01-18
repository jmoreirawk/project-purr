plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "pro.moreira.projectpurr.common.ui.assets"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
}

dependencies {
    // Compose
    with(libs.compose) {
        implementation(platform(bom))
        implementation(ui)
        implementation(activity)
        implementation(material3)
        implementation(material)
        implementation(navigation)
    }
    // Images
    implementation(libs.landscapist)
}
