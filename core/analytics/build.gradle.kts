import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    id("trace.android.library")
    id("trace.android.compose")
}

android {
    namespace = "com.virtuous.analytics"

    defaultConfig {
        val properties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

        buildConfigField(
            "String",
            "AMPLITUDE_API_KEY",
            properties["AMPLITUDE_API_KEY"] as String,
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.amplitude.analytics)
}

