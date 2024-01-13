import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin

plugins {
    alias(libs.plugins.application).apply(false)
    alias(libs.plugins.kotlin).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.hilt).apply(false)
}

subprojects {
    val libraryConfig: Any.() -> Unit = {
        extensions.configure<LibraryExtension> {
            defaultConfig {
                testInstrumentationRunner = App.Android.TestRunner.default
            }

            configureLibraries()
        }
    }

    plugins.withType<LibraryPlugin>(libraryConfig)
}

fun LibraryExtension.configureLibraries() {
    compileSdk = App.Android.compileSdk

    defaultConfig {
        minSdk = App.Android.minSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(App.Java.version)
        targetCompatibility = JavaVersion.toVersion(App.Java.version)
    }
}