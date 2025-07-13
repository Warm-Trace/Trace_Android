package com.virtuous.splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.virtuous.navigation.SplashRoute
import com.virtuous.splash.SplashRoute


fun NavGraphBuilder.splashScreen(

) {
    composable<SplashRoute> {
        SplashRoute()
    }
}