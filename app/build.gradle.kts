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
        versionCode = 10
        versionName = "2.5.0"
    }

    // A stable signing identity is required so the rolling-release APK can
    // install over previous versions. Default: the self-signed keystore
    // committed to the repo (this is a sideload-distributed hobby launcher,
    // not a Play Store key). GitHub Secrets, when configured, override it.
    val envKeystorePath: String? = System.getenv("NS_KEYSTORE_PATH")

    signingConfigs {
        create("release") {
            if (envKeystorePath != null) {
                storeFile = file(envKeystorePath)
                storePassword = System.getenv("NS_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("NS_KEY_ALIAS")
                keyPassword = System.getenv("NS_KEY_PASSWORD")
            } else {
                storeFile = rootProject.file("signing/neversoft-release.keystore")
                storePassword = "neversoft11"
                keyAlias = "neversoft"
                keyPassword = "neversoft11"
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
            signingConfig = signingConfigs.getByName("release")
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
