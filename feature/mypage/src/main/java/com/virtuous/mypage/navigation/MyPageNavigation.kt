package com.virtuous.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.virtuous.common.ui.defaultSlideFadeIn
import com.virtuous.common.ui.defaultSlideFadeOut
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.mypage.graph.mypage.MyPageRoute
import com.virtuous.mypage.graph.setting.SettingRoute
import com.virtuous.mypage.graph.updateprofile.UpdateProfileRoute
import com.virtuous.mypage.graph.webview.WebViewRoute
import com.virtuous.navigation.MyPageBaseRoute
import com.virtuous.navigation.MyPageGraph

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.MyPageRoute, navOptions)
}

fun NavController.navigateToUpdateProfile(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.UpdateProfileRoute, navOptions)
}

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.SettingRoute, navOptions)
}

fun NavController.navigateToWebView(url: String, navOptions: NavOptions? = null) {
    navigate(MyPageGraph.WebViewRoute(url), navOptions)
}

fun NavGraphBuilder.myPageNavGraph(
    navigateToLogin: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateToUpdateProfile: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToWebView: (String) -> Unit,
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
                if (initialState.destination.route?.contains(MyPageGraph.WebViewRoute::class.simpleName.toString()) == true) {
                    null
                } else {
                    defaultSlideFadeIn()
                }
            },
            exitTransition = {
                if (targetState.destination.route?.contains(MyPageGraph.WebViewRoute::class.simpleName.toString()) == true) {
                    null
                } else {
                    defaultSlideFadeOut()
                }
            }
        ) {
            SettingRoute(
                navigateToLogin = navigateToLogin,
                navigateToWebView = navigateToWebView,
                navigateBack = navigateBack
            )
        }

        composable<MyPageGraph.WebViewRoute> { backStackEntry ->
            val webView = backStackEntry.toRoute<MyPageGraph.WebViewRoute>()
            WebViewRoute(
                url = webView.url,

            )
        }
    }
}