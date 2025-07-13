plugins {
    id("trace.android.feature")
}

android {
    namespace = "com.virtuous.mission"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}

