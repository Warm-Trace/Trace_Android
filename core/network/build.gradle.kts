import java.io.FileInputStream
import java.util.Properties

plugins {
    id("trace.android.library")
    id("trace.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.virtuous.network"

    defaultConfig {
        val properties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

        buildConfigField(
            "String",
            "TRACE_BASE_URL",
            properties["TRACE_BASE_URL"] as String
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.okhttp.logging)

    implementation(libs.firebase.messaging)
}