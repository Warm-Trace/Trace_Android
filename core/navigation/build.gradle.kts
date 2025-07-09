plugins {
    id("trace.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
}