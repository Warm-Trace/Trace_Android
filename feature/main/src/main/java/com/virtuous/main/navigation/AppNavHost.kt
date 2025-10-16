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
import com.virtuous.home.navigation.navigateToNotification
import com.virtuous.home.navigation.navigateToPost
import com.virtuous.home.navigation.navigateToSearch
import com.virtuous.home.navigation.navigateToUpdatePost
import com.virtuous.home.navigation.navigateToUserProfile
import com.virtuous.home.navigation.navigateToWritePost
import com.virtuous.mission.navigation.missionNavGraph
import com.virtuous.mission.navigation.navigateToMission
import com.virtuous.mission.navigation.navigateToVerifyMission
import com.virtuous.mypage.navigation.myPageNavGraph
import com.virtuous.mypage.navigation.navigateToBlockedUser
import com.virtuous.mypage.navigation.navigateToSetting
import com.virtuous.mypage.navigation.navigateToSwallow
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
        splashScreen()

        authNavGraph(
            navigateToHome = {
                navController.navigateToHome(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                )
            },
            navigateBack = navController::popBackStack,
            navigateToEditProfile = { signUpToken, providerId ->
                navController.navigateToEditProfile(signUpToken, providerId, navOptions {
                    launchSingleTop = true
                })
            }
        )


        homeNavGraph(
            navigateToPost = { postDetail ->
                navController.navigateToPost(postDetail, navOptions {
                    launchSingleTop = true
                })
            },
            navigateToWritePost = {
                navController.navigateToWritePost(navOptions = navOptions {
                    launchSingleTop = true
                })
            },
            navigateToUpdatePost = { postId ->
                navController.navigateToUpdatePost(postId = postId, navOptions {
                    launchSingleTop = true
                })
            },
            navigateToSearch = {
                navController.navigateToSearch(navOptions = navOptions {
                    launchSingleTop = true
                })
            },
            navigateToPostReplacing = { postDetail ->
                navController.navigateToPost(postDetail, navOptions = navOptions {
                    popUpTo<HomeGraph.HomeRoute>()
                    launchSingleTop = true
                })
            },
            navigateToUserProfile = { userId ->
                navController.navigateToUserProfile(userId, navOptions {
                    launchSingleTop = true
                })
            },
            navigateToNotification = {
                navController.navigateToNotification(navOptions {
                    launchSingleTop = true
                })
            },
            navigateToPostById = { postId ->
                navController.navigateToPost(postId, navOptions {
                    launchSingleTop = true
                })
            },
            navigateBack = navController::popBackStack,
        )

        missionNavGraph(
            navigateToPost = { postId ->
                navController.navigateToPost(postId, navOptions = navOptions {
                    popUpTo(MissionGraph.MissionRoute)
                    launchSingleTop = true
                })
            },
            navigateToVerifyMission = { missionId ->
                navController.navigateToVerifyMission(missionId, navOptions {
                    launchSingleTop = true
                })
            },
            navigateBack = navController::popBackStack
        )

        myPageNavGraph(
            navigateToPost = { postId ->
                navController.navigateToPost(postId, navOptions {
                    launchSingleTop = true
                })
            },
            navigateToUpdateProfile = {
                navController.navigateToUpdateProfile(navOptions {
                    launchSingleTop = true
                })
            },
            navigateToSetting = {
                navController.navigateToSetting(navOptions {
                    launchSingleTop = true
                })
            },
            navigateToLogin = {
                navController.navigateToLogin(navOptions {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                })
            },
            navigateToWebView = { url ->
                navController.navigateToWebView(url, navOptions {
                    launchSingleTop = true
                })
            },
            navigateToBlockedUser = {
                navController.navigateToBlockedUser(navOptions {
                    launchSingleTop = true
                })
            },
            navigateToUserProfile = { userId ->
                navController.navigateToUserProfile(userId, navOptions {
                    launchSingleTop = true
                })
            },
            navigateToSwallow = {
                navController.navigateToSwallow(navOptions {
                    launchSingleTop = true
                })
            },
            navigateBack = navController::popBackStack,
        )
    }
}