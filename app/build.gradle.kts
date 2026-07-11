plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.neversoft.launcher"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.neversoft.launcher"
        minSdk = 29
        targetSdk = 35
        versionCode = 5
        versionName = "2.1.0"
    }

    // Keystore comes from the environment (CI injects it from GitHub
    // Secrets, or generates a throwaway one). Nothing secret in the repo.
    val envKeystorePath: String? = System.getenv("NS_KEYSTORE_PATH")

    signingConfigs {
        create("release") {
            if (envKeystorePath != null) {
                storeFile = file(envKeystorePath)
                storePassword = System.getenv("NS_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("NS_KEY_ALIAS")
                keyPassword = System.getenv("NS_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = if (envKeystorePath != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.android)
    implementation(libs.window)
    debugImplementation(libs.compose.ui.tooling)
}
