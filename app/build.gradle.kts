import java.util.Properties

plugins {
    id("trace.android.application")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.virtuous.trace"

    defaultConfig {
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    defaultConfig {
        val properties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
           properties["KAKAO_NATIVE_APP_KEY"] as String
        )

        manifestPlaceholders["KAKAO_REDIRECT_URI"] = properties["KAKAO_REDIRECT_URI"] as String
    }

    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(libs.kakao.user)
    implementation(libs.firebase.messaging)
}