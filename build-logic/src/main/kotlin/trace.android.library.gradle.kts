import com.virtuous.app.configureCoroutineAndroid
import com.virtuous.app.configureHiltAndroid
import com.virtuous.app.configureKotest
import com.virtuous.app.configureKotlinAndroid


plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotest()
configureCoroutineAndroid()
