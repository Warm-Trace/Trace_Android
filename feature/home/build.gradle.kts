plugins {
    id("trace.android.feature")
}

android {
    namespace = "com.virtuous.home"

}

dependencies {
    implementation(projects.core.datastore)

    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    implementation(libs.kotlinx.datetime)
}