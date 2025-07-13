import com.virtuous.app.configureHiltAndroid
import com.virtuous.app.configureKotestAndroid
import com.virtuous.app.configureKotlinAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotestAndroid()