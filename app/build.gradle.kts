import com.example.ktor_websocket_application.JAVA_VERSION
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(notation = libs.plugins.android.application)
    alias(notation = libs.plugins.kotlin.android)
    alias(notation = libs.plugins.kotlin.compose)
    alias(notation = libs.plugins.kotlin.serialization)
    alias(notation = libs.plugins.kotlin.symbol.processor)
    alias(notation = libs.plugins.hilt.android)
}

kotlin {
    jvmToolchain(jdkVersion = JAVA_VERSION)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(target = JAVA_VERSION.toString()))
        freeCompilerArgs.add("-Xannotation-target-all")
    }
}

android {
    namespace = "com.example.ktor_websocket_application"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ktor_websocket_application"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

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
    packaging {
        resources {
            excludes += "META-INF/*"
        }
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
    implementation(dependencyNotation = libs.androidx.foundation)
    testImplementation(dependencyNotation = libs.junit)
    androidTestImplementation(dependencyNotation = libs.androidx.junit)
    androidTestImplementation(dependencyNotation = libs.androidx.espresso.core)
    androidTestImplementation(dependencyNotation = platform(libs.androidx.compose.bom))
    androidTestImplementation(dependencyNotation = libs.androidx.ui.test.junit4)
    debugImplementation(dependencyNotation = libs.androidx.ui.tooling)
    debugImplementation(dependencyNotation = libs.androidx.ui.test.manifest)

    // Compose
    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.compose)
    implementation(dependencyNotation = libs.androidx.navigation.compose)

    // Coroutines
    implementation(dependencyNotation = libs.kotlinx.coroutines.core)
    implementation(dependencyNotation = libs.kotlinx.coroutines.android)

    // Coroutine Lifecycle Scopes
    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.ktx)

    // Dagger - Hilt
    implementation(dependencyNotation = libs.hilt.android)
    ksp(dependencyNotation = libs.hilt.android.compiler)
    implementation(dependencyNotation = libs.androidx.hilt.lifecycle.viewmodel)
    ksp(dependencyNotation = libs.androidx.hilt.compiler)
    implementation(dependencyNotation = libs.androidx.hilt.navigation.compose)

    // Ktor
    implementation(dependencyNotation = libs.ktor.client.core)
    implementation(dependencyNotation = libs.ktor.client.cio)
    implementation(dependencyNotation = libs.ktor.client.serialization)
    implementation(dependencyNotation = libs.ktor.client.websockets)
    implementation(dependencyNotation = libs.ktor.client.logging)
    implementation(dependencyNotation = libs.ktor.client.content.negotiation)
    implementation(dependencyNotation = libs.ktor.serialization.kotlinx.json)
    implementation(dependencyNotation = libs.slf4j.api)
    debugImplementation(dependencyNotation = libs.logback.android)

    implementation(dependencyNotation = libs.kotlinx.serialization.json)

    implementation(dependencyNotation = libs.kotlinx.datetime)
}
