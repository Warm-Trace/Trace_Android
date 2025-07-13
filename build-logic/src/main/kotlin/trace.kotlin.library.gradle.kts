import com.virtuous.app.configureKotest
import com.virtuous.app.configureKotlin
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
}

configureKotlin()
configureKotest()
