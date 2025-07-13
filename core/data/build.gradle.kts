plugins {
    id("trace.android.library")
    id("trace.android.hilt")
}

android {
    namespace = "com.virtuous.data"
}

dependencies {
    implementation(libs.androidx.exifinterface)
    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
}