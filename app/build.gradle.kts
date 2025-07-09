import com.example.ktor_websocket_application.JAVA_VERSION
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(notation = libs.plugins.android.application)
    alias(notation = libs.plugins.kotlin.android)
    alias(notation = libs.plugins.kotlin.compose)
    alias(notation = libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(jdkVersion = JAVA_VERSION)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(target = JAVA_VERSION.toString()))
    }
}

android {
    namespace = "com.example.ktor_websocket_application"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ktor_websocket_application"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(dependencyNotation = libs.androidx.core.ktx)
    implementation(dependencyNotation = libs.androidx.lifecycle.runtime.ktx)
    implementation(dependencyNotation = libs.androidx.activity.compose)
    implementation(dependencyNotation = platform(libs.androidx.compose.bom))
    implementation(dependencyNotation = libs.androidx.ui)
    implementation(dependencyNotation = libs.androidx.ui.graphics)
    implementation(dependencyNotation = libs.androidx.ui.tooling.preview)
    implementation(dependencyNotation = libs.androidx.material3)
    testImplementation(dependencyNotation = libs.junit)
    androidTestImplementation(dependencyNotation = libs.androidx.junit)
    androidTestImplementation(dependencyNotation = libs.androidx.espresso.core)
    androidTestImplementation(dependencyNotation = platform(libs.androidx.compose.bom))
    androidTestImplementation(dependencyNotation = libs.androidx.ui.test.junit4)
    debugImplementation(dependencyNotation = libs.androidx.ui.tooling)
    debugImplementation(dependencyNotation = libs.androidx.ui.test.manifest)

    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.compose)

    implementation(dependencyNotation = libs.kotlinx.coroutines.core)
    implementation(dependencyNotation = libs.kotlinx.coroutines.android)

    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.ktx)

    implementation(dependencyNotation = libs.ktor.client.core)
    implementation(dependencyNotation = libs.ktor.client.cio)
    implementation(dependencyNotation = libs.ktor.client.serialization)
    implementation(dependencyNotation = libs.ktor.client.websockets)
    implementation(dependencyNotation = libs.ktor.client.logging)
    debugImplementation(dependencyNotation = libs.logback.classic)

    implementation(dependencyNotation = libs.kotlinx.serialization.json)
}
