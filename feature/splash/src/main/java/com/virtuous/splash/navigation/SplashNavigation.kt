package com.virtuous.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.virtuous.navigation.SplashRoute
import com.virtuous.splash.SplashRoute


fun NavGraphBuilder.splashScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    composable<SplashRoute> {
        SplashRoute(
            navigateToLogin = navigateToLogin,
            navigateToHome = navigateToHome
        )
    }
}