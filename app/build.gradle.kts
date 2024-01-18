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
        versionCode = App.Android.versionCode
        versionName = App.Android.versionName

        testInstrumentationRunner = App.Android.TestRunner.default
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
    implementation(project(":feature:list"))
    implementation(project(":feature:details"))
    // Android
    implementation(libs.androidx.ktx.core)
    // Compose
    with(libs.compose) {
        implementation(platform(bom))
        implementation(ui)
        implementation(material)
        implementation(preview)
        implementation(activity)
        implementation(navigation)
    }
    // Dependency Injection
    with(libs.hilt) {
        implementation(android)
        ksp(compiler)
    }
}
