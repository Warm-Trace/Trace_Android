import java.util.Properties

plugins {
    id("trace.android.feature")
}

android {
    namespace = "com.virtuous.mypage"

    defaultConfig {
        val properties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }
        buildConfigField(
            "String",
            "TRACE_INQUIRY_URL",
            properties["TRACE_INQUIRY_URL"] as String
        )
        buildConfigField(
            "String",
            "TRACE_PRIVACY_POLICY_URL",
            properties["TRACE_PRIVACY_POLICY_URL"] as String
        )
    }

    buildFeatures {
        buildConfig = true
    }

}


dependencies {
    implementation(libs.kakao.user)
    implementation(libs.coil.compose)
}
