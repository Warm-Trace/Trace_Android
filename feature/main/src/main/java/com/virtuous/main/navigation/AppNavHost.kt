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
                    }
                )
            },
            navigateBack = { navigateBack(navController) },
            navigateToEditProfile = navController::navigateToEditProfile
        )

        homeNavGraph(
            navigateToPost = navController::navigateToPost,
            navigateToWritePost = navController::navigateToWritePost,
            navigateToUpdatePost = navController::navigateToUpdatePost,
            navigateToSearch = navController::navigateToSearch,
            navigateToPostReplacing = { postDetail ->
                navController.navigateToPost(postDetail, navOptions = navOptions {
                    popUpTo<HomeGraph.HomeRoute>()
                })
            },
            navigateToUserProfile = navController::navigateToUserProfile,
            navigateToNotification = navController::navigateToNotification,
            navigateToPostById = { postId ->
                navController.navigateToPost(postId)
            },
            navigateToMission = navController::navigateToMission,
            navigateBack = { navigateBack(navController) },
        )

        missionNavGraph(
            navigateToPost = { postId ->
                navController.navigateToPost(postId, navOptions = navOptions {
                    popUpTo(MissionGraph.MissionRoute)
                })
            },
            navigateToVerifyMission = navController::navigateToVerifyMission,
            navigateBack = { navigateBack(navController) }
        )

        myPageNavGraph(
            navigateToPost = navController::navigateToPost,
            navigateToUpdateProfile = navController::navigateToUpdateProfile,
            navigateToSetting = navController::navigateToSetting,
            navigateBack = { navigateBack(navController) },
            navigateToLogin = {
                navController.navigateToLogin(navOptions {
                    popUpTo(0) { inclusive = true }
                })
            },
            navigateToWebView = navController::navigateToWebView,
            navigateToBlockedUser = navController::navigateToBlockedUser,
            navigateToUserProfile = navController::navigateToUserProfile
        )

    }
}

private fun navigateBack(
    navController: NavHostController
) {
    navController.popBackStack()
}
 