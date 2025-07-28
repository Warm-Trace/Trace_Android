plugins {
    id("trace.android.library")
    id("trace.android.compose")
}

android {
    namespace = "com.virtuous.designsystem"
}

dependencies {
    implementation(projects.core.commonUi)
    implementation(projects.core.domain)

    implementation(libs.coil.compose)
}


