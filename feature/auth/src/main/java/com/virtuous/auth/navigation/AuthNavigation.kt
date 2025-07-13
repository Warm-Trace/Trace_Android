package com.virtuous.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.virtuous.auth.graph.editprofile.EditProfileRoute
import com.virtuous.auth.graph.login.LoginRoute
import com.virtuous.navigation.AuthGraph
import com.virtuous.navigation.AuthGraphBaseRoute



fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(AuthGraph.LoginRoute, navOptions)
}

fun NavController.navigateToEditProfile(signUpToken : String, providerId : String, navOptions: NavOptions? = null) {
    navigate(AuthGraph.EditProfileRoute(signUpToken, providerId), navOptions)
}

fun NavGraphBuilder.authNavGraph(
    navigateToHome: () -> Unit,
    navigateToEditProfile: (String, String) -> Unit,
    navigateBack: () -> Unit
) {
    navigation<AuthGraphBaseRoute>(startDestination = AuthGraph.LoginRoute) {
        composable<AuthGraph.LoginRoute> {
            LoginRoute(
                navigateToHome = navigateToHome,
                navigateToEditProfile = { signUpToken, providerId -> navigateToEditProfile(signUpToken, providerId) }
            )
        }

        composable<AuthGraph.EditProfileRoute> {
            EditProfileRoute(
                navigateToHome = navigateToHome,
                navigateBack = navigateBack
            )
        }

    }
}
