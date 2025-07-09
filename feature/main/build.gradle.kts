plugins {
    id("trace.android.feature")
}

android {
    namespace = "com.example.main"
}

dependencies {
    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.mypage)
    implementation(projects.feature.mission)
    implementation(projects.feature.splash)

    implementation(libs.kakao.user)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    implementation(libs.kotlinx.datetime)
}