plugins {
    id("trace.android.library")
    id("trace.kotlin.hilt")
}

android {
    namespace = "com.virtuous.domain"
}

dependencies {
    implementation(libs.androidx.paging.runtime)

    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
