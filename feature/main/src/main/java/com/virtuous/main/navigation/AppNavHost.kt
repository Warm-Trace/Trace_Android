package com.virtuous.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.virtuous.auth.navigation.authNavGraph
import com.virtuous.auth.navigation.navigateToEditProfile
import com.virtuous.auth.navigation.navigateToLogin
import com.virtuous.home.navigation.homeNavGraph
import com.virtuous.home.navigation.navigateToHome
import com.virtuous.home.navigation.navigateToPost
import com.virtuous.home.navigation.navigateToSearch
import com.virtuous.home.navigation.navigateToUpdatePost
import com.virtuous.home.navigation.navigateToWritePost
import com.virtuous.mission.navigation.missionNavGraph
import com.virtuous.mission.navigation.navigateToVerifyMission
import com.virtuous.mypage.navigation.myPageNavGraph
import com.virtuous.mypage.navigation.navigateToSetting
import com.virtuous.mypage.navigation.navigateToUpdateProfile
import com.virtuous.mypage.navigation.navigateToWebView
import com.virtuous.navigation.HomeGraph
import com.virtuous.navigation.MissionGraph
import com.virtuous.navigation.SplashRoute
import com.virtuous.splash.navigation.splashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
    ) {
        val currentRoute = navController.currentDestination?.route

        splashScreen()

        authNavGraph(
            navigateToHome = {
                navController.navigateToHome(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                    }
                )
            },
            navigateBack = { navigateBack(navController) },
            navigateToEditProfile = { signUpToken, providerId ->
                navController.navigateToEditProfile(signUpToken, providerId)
            }
        )

        homeNavGraph(
            navigateToPost = { postId ->
                navController.navigateToPost(postId)
            },
            navigateToWritePost = {
                navController.navigateToWritePost()
            },
            navigateToUpdatePost = { postId ->
                navController.navigateToUpdatePost(postId)
            },
            navigateToSearch = {
                navController.navigateToSearch()
            },
            navigateToPostReplacing = { postDetail ->
                navController.navigateToPost(postDetail, navOptions = navOptions {
                    popUpTo<HomeGraph.HomeRoute>()
                })
            },
            navigateBack = { navigateBack(navController) },
        )

        missionNavGraph(
            navigateToPost = { postId ->
                navController.navigateToPost(postId, navOptions = navOptions {
                    popUpTo(MissionGraph.MissionRoute)
                })
            },
            navigateToVerifyMission = { description ->
                navController.navigateToVerifyMission(description)
            },
            navigateBack = { navigateBack(navController) }
        )

        myPageNavGraph(
            navigateToPost = { postFeed -> navController.navigateToPost(postFeed) },
            navigateToUpdateProfile = { navController.navigateToUpdateProfile() },
            navigateToSetting = { navController.navigateToSetting() },
            navigateBack = { navigateBack(navController) },
            navigateToLogin = {
                navController.navigateToLogin(navOptions {
                    popUpTo(0) { inclusive = true }
                })
            },
            navigateToWebView = { url ->
                navController.navigateToWebView(url)
            }
        )

    }
}

private fun navigateBack(
    navController: NavHostController
) {
    navController.popBackStack()
}
 