plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0-Beta2"
    kotlin("plugin.serialization") version "2.3.10"
}

android {
    namespace = "com.example.pos"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.pos"
        minSdk = 31
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.5.0-alpha18")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Runtime Compose agar bisa collect state dengan lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Supabase BOM
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))

    // Supabase Auth
    implementation("io.github.jan-tennert.supabase:auth-kt")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Ktor Android Client
    implementation("io.ktor:ktor-client-android:3.0.3")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    debugImplementation(libs.androidx.ui.tooling)

    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    implementation("androidx.compose.material:material-icons-extended:1.7.5")
}