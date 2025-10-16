import java.util.Properties

plugins {
    id("trace.android.application")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.android.application)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.virtuous.trace"

    defaultConfig {
        versionCode = 8
        versionName = "1.0.7"
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    defaultConfig {
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            localProperties["KAKAO_NATIVE_APP_KEY"] as String
        )

        manifestPlaceholders["KAKAO_REDIRECT_URI"] = localProperties["KAKAO_REDIRECT_URI"] as String
    }

    signingConfigs {
        val keystoreProperties = Properties()
        keystoreProperties.load(rootProject.file("keystore.properties").bufferedReader())

        create("release") {
            storeFile = file(keystoreProperties["STORE_PATH"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
            keyAlias = keystoreProperties["KEY_ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            manifestPlaceholders["APP_NAME"] = "@string/app_name"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            manifestPlaceholders["APP_NAME"] = "@string/app_name_debug"
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
    }


    buildFeatures {
        buildConfig = true
    }
}

baselineProfile {
    dexLayoutOptimization = true
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    baselineProfile(projects.baselineprofile)

    implementation(libs.kakao.user)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.profileinstaller)

}