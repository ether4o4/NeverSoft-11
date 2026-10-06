plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Keep dispatcher regressions on the existing JVM/Robolectric test route.
project(":app") {
    plugins.withId("com.android.application") {
        dependencies {
            add("testImplementation", platform(libs.compose.bom))
            add("testImplementation", "androidx.compose.ui:ui-test-junit4")
            add("debugImplementation", "androidx.compose.ui:ui-test-manifest")
        }
    }
}
