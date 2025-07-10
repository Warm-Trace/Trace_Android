package com.example.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.example.auth.navigation.authNavGraph
import com.example.auth.navigation.navigateToEditProfile
import com.example.auth.navigation.navigateToLogin
import com.example.home.navigation.homeNavGraph
import com.example.home.navigation.navigateToHome
import com.example.home.navigation.navigateToPost
import com.example.home.navigation.navigateToSearch
import com.example.home.navigation.navigateToUpdatePost
import com.example.home.navigation.navigateToWritePost
import com.example.mission.navigation.missionNavGraph
import com.example.mission.navigation.navigateToVerifyMission
import com.example.mypage.navigation.myPageNavGraph
import com.example.mypage.navigation.navigateToSetting
import com.example.mypage.navigation.navigateToUpdateProfile
import com.example.mypage.navigation.navigateToWebView
import com.example.navigation.HomeGraph
import com.example.navigation.MissionGraph
import com.example.navigation.SplashRoute
import com.example.splash.navigation.splashScreen

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
 