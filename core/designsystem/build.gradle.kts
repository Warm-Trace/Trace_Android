plugins {
    id("trace.android.library")
    id("trace.android.compose")
}

android {
    namespace = "com.virtuous.designsystem"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)

    implementation(libs.coil.compose)
}


