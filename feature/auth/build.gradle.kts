plugins {
    id("trace.android.feature")
}

android {
    namespace = "com.virtuous.auth"

}

dependencies {
    implementation(libs.kakao.user)
    implementation(libs.coil.compose)
}