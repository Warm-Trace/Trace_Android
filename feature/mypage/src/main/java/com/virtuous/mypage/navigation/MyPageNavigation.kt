package com.virtuous.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.virtuous.common_ui.ui.defaultSlideFadeIn
import com.virtuous.common_ui.ui.defaultSlideFadeOut
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.mypage.graph.blocked_user.BlockedUserRoute
import com.virtuous.mypage.graph.mypage.MyPageRoute
import com.virtuous.mypage.graph.setting.SettingRoute
import com.virtuous.mypage.graph.updateprofile.UpdateProfileRoute
import com.virtuous.mypage.graph.webview.WebViewRoute
import com.virtuous.navigation.MyPageBaseRoute
import com.virtuous.navigation.MyPageGraph
import com.virtuous.navigation.containsRoute

fun NavController.navigateToUpdateProfile(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.UpdateProfileRoute, navOptions)
}

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.SettingRoute, navOptions)
}

fun NavController.navigateToWebView(url: String, navOptions: NavOptions? = null) {
    navigate(MyPageGraph.WebViewRoute(url), navOptions)
}

fun NavController.navigateToBlockedUser(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.BlockedUserRoute, navOptions)
}

fun NavGraphBuilder.myPageNavGraph(
    navigateToLogin: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateToUpdateProfile: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToWebView: (String) -> Unit,
    navigateToBlockedUser: () -> Unit,
    navigateToUserProfile: (String) -> Unit,
    navigateBack: () -> Unit
) {
    navigation<MyPageBaseRoute>(startDestination = MyPageGraph.MyPageRoute) {
        composable<MyPageGraph.MyPageRoute> {
            MyPageRoute(
                navigateToPost = navigateToPost,
                navigateToEditProfile = navigateToUpdateProfile,
                navigateToSetting = navigateToSetting
            )
        }

        composable<MyPageGraph.UpdateProfileRoute>(
            enterTransition = { defaultSlideFadeIn() },
            exitTransition = { defaultSlideFadeOut() }
        ) {
            UpdateProfileRoute(
                navigateBack = navigateBack
            )
        }

        composable<MyPageGraph.SettingRoute>(
            enterTransition = {
                if (initialState.destination.containsRoute(listOf(MyPageGraph.WebViewRoute::class, MyPageGraph.BlockedUserRoute::class))) {
                    null
                } else {
                    defaultSlideFadeIn()
                }
            },
            exitTransition = {
                if (targetState.destination.containsRoute(listOf(MyPageGraph.WebViewRoute::class, MyPageGraph.BlockedUserRoute::class))) {
                    null
                } else {
                    defaultSlideFadeOut()
                }
            }
        ) {
            SettingRoute(
                navigateToLogin = navigateToLogin,
                navigateToWebView = navigateToWebView,
                navigateBack = navigateBack,
                navigateToBlockedUser = navigateToBlockedUser
            )
        }

        composable<MyPageGraph.BlockedUserRoute>(
            enterTransition = { defaultSlideFadeIn() },
            exitTransition = { defaultSlideFadeOut() }
        ) {
            BlockedUserRoute(
                navigateBack = navigateBack,
                navigateToUserProfile = navigateToUserProfile
            )
        }

        composable<MyPageGraph.WebViewRoute> { backStackEntry ->
            val webView = backStackEntry.toRoute<MyPageGraph.WebViewRoute>()
            WebViewRoute(url = webView.url)
        }
    }
}