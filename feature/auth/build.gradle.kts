plugins {
    id("trace.android.feature")
}

android {
    namespace = "com.example.auth"

}

dependencies {
    implementation(libs.kakao.user)
    implementation(libs.coil.compose)
    implementation(libs.navigation.compose)
}