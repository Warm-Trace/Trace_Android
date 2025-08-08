plugins {
    id("trace.android.library")
    id("trace.android.hilt")
}

android {
    namespace = "com.virtuous.datastore"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.datastore)
    implementation(libs.gson)
}