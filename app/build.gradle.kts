plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = App.Java.version
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
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
        kapt(compiler)
    }
}

kapt {
    correctErrorTypes = true
}
