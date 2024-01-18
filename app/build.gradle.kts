plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
}

android {
    namespace = App.Android.applicationId
    compileSdk = App.Android.compileSdk

    defaultConfig {
        applicationId = App.Android.applicationId
        minSdk = App.Android.minSdk
        targetSdk = App.Android.targetSdk
        versionCode = App.Android.versionCode
        versionName = App.Android.versionName

        testInstrumentationRunner = "pro.moreira.projectpurr.utils.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Modules
    implementation(project(":common:ui:assets"))
    implementation(project(":feature:list"))
    implementation(project(":feature:details"))
    // Android
    implementation(libs.androidx.ktx.core)
    // Compose
    with(libs.compose) {
        implementation(platform(bom))
        implementation(ui)
        implementation(material3)
        implementation(activity)
        implementation(navigation)
    }
    // Dependency Injection
    with(libs.hilt) {
        implementation(android)
        ksp(compiler)
    }
    // Testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.test)
    debugImplementation(libs.compose.manifest)
    androidTestImplementation(libs.hilt.testing)
    ksp(libs.hilt.testing.compiler)
    androidTestImplementation(libs.androidx.test.runner)
}
