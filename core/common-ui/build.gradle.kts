plugins {
    id("trace.android.library")
    id("trace.android.compose")
}


android {
    namespace = "com.virtuous.common_ui"
}
dependencies {
    implementation(libs.androidx.paging.compose)
}



