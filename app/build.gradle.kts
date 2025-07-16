import java.util.Properties

plugins {
    id("trace.android.application")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.virtuous.trace"

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
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
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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