package com.virtuous.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.virtuous.common_ui.ui.defaultSlideDownFadeOut
import com.virtuous.common_ui.ui.defaultSlideFadeIn
import com.virtuous.common_ui.ui.defaultSlideFadeOut
import com.virtuous.common_ui.ui.defaultSlideUpFadeIn
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.home.graph.home.HomeRoute
import com.virtuous.home.graph.notification.NotificationRoute
import com.virtuous.home.graph.post.PostRoute
import com.virtuous.home.graph.search.SearchRoute
import com.virtuous.home.graph.updatepost.UpdatePostRoute
import com.virtuous.home.graph.user.UserProfileRoute
import com.virtuous.home.graph.writepost.WritePostRoute
import com.virtuous.navigation.HomeBaseRoute
import com.virtuous.navigation.HomeGraph
import com.virtuous.navigation.containsRoute
import java.time.format.DateTimeFormatter

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HomeGraph.HomeRoute, navOptions)
}

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    navigate(HomeGraph.SearchRoute, navOptions)
}

fun NavController.navigateToWritePost(navOptions: NavOptions? = null) {
    navigate(HomeGraph.WritePostRoute, navOptions)
}

fun NavController.navigateToPost(postFeed: PostFeed, navOptions: NavOptions? = null) {
    navigate(
        HomeGraph.PostRoute(
            postId = postFeed.postId,
            providerId = postFeed.providerId,
            postType = postFeed.postType.name,
            title = postFeed.title,
            isVerified = postFeed.isVerified,
            content = postFeed.content,
            profileImageUrl = postFeed.profileImageUrl,
            imageUrl = postFeed.imageUrl,
            nickname = postFeed.nickname,
            viewCount = postFeed.viewCount,
            createdAt = postFeed.createdAt.toString()
        ), navOptions
    )
}

fun NavController.navigateToPost(postDetail: PostDetail, navOptions: NavOptions? = null) {
    navigate(
        HomeGraph.PostRoute(
            postId = postDetail.postId,
            postType = postDetail.postType.name,
            title = postDetail.title,
            isVerified = postDetail.isVerified,
            content = postDetail.content,
            profileImageUrl = postDetail.profileImageUrl,
            imageUrl = postDetail.images.firstOrNull(),
            nickname = postDetail.nickname,
            viewCount = postDetail.viewCount,
            createdAt = postDetail.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            missionContent = postDetail.missionContent,
            providerId = postDetail.providerId,
            isOwner = postDetail.isOwner,
            yourEmotionType = postDetail.yourEmotionType?.label
        ), navOptions
    )
}

fun NavController.navigateToPost(postId: Int, navOptions: NavOptions? = null) {
    navigate(
        HomeGraph.PostRoute(
            postId = postId,
        ), navOptions
    )
}


fun NavController.navigateToUpdatePost(postId: Int, navOptions: NavOptions? = null) {
    navigate(HomeGraph.UpdatePostRoute(postId), navOptions)
}

fun NavController.navigateToUserProfile(providerId: String, navOptions: NavOptions? = null) {
    navigate(HomeGraph.UserProfileRoute(providerId), navOptions)
}

fun NavController.navigateToNotification(navOptions: NavOptions? = null) {
    navigate(HomeGraph.NotificationRoute, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    navigateToSearch: () -> Unit,
    navigateToPostById: (Int) -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateToPostReplacing: (PostDetail) -> Unit,
    navigateToWritePost: () -> Unit,
    navigateToUpdatePost: (Int) -> Unit,
    navigateToUserProfile: (String) -> Unit,
    navigateToNotification: () -> Unit,
    navigateToMission: () -> Unit,
    navigateBack: () -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeGraph.HomeRoute) {
        composable<HomeGraph.HomeRoute> {
            HomeRoute(
                navigateToPost = { postFeed -> navigateToPost(postFeed) },
                navigateToWritePost = navigateToWritePost,
                navigateToSearch = navigateToSearch,
                navigateToNotification = navigateToNotification
            )
        }

        composable<HomeGraph.SearchRoute>(
            enterTransition = {
                defaultSlideFadeIn()
            },
            exitTransition = {
                defaultSlideFadeOut()
            },
        ) {
            SearchRoute(
                navigateBack = navigateBack,
                navigateToPost = { postFeed -> navigateToPost(postFeed) },
            )
        }

        composable<HomeGraph.WritePostRoute>(
            enterTransition = {
                defaultSlideUpFadeIn()
            },
            exitTransition = {
                defaultSlideDownFadeOut()
            },
        ) {
            WritePostRoute(
                navigateToPost = { postDetail -> navigateToPostReplacing(postDetail) },
                navigateBack = navigateBack
            )
        }

        composable<HomeGraph.PostRoute>(
            enterTransition = {
                if (initialState.destination.containsRoute(listOf(HomeGraph.UpdatePostRoute::class))) {
                    null
                } else {
                    defaultSlideFadeIn()
                }
            },
            exitTransition = {
                if (targetState.destination.containsRoute(
                        listOf(
                            HomeGraph.UpdatePostRoute::class,
                            HomeGraph.UserProfileRoute::class
                        )
                    )
                ) {
                    null
                } else {
                    defaultSlideFadeOut()
                }
            },
            popEnterTransition = null,
            popExitTransition = { defaultSlideFadeOut() }
        ) {
            PostRoute(
                navigateBack = navigateBack,
                navigateToUpdatePost = navigateToUpdatePost,
                navigateToUserProfile = navigateToUserProfile
            )
        }

        composable<HomeGraph.UpdatePostRoute>(
            enterTransition = {
                defaultSlideUpFadeIn()
            },
            exitTransition = {
                defaultSlideDownFadeOut()
            },
        ) {
            UpdatePostRoute(
                navigateBack = navigateBack,
                navigateToPost = { postDetail -> navigateToPostReplacing(postDetail) }
            )
        }

        composable<HomeGraph.UserProfileRoute>(
            enterTransition = { defaultSlideFadeIn() },
            exitTransition = {
                if (targetState.destination.containsRoute(listOf(HomeGraph.PostRoute::class))) {
                    null
                } else {
                    defaultSlideFadeOut()
                }
            },
            popEnterTransition = null,
            popExitTransition = { defaultSlideFadeOut() }
        ) {
            UserProfileRoute(
                navigateBack = navigateBack,
                navigateToPost = { postFeed -> navigateToPost(postFeed) }
            )
        }

        composable<HomeGraph.NotificationRoute>(
            enterTransition = { defaultSlideFadeIn() },
            exitTransition = { defaultSlideFadeOut() }
        ) {
            NotificationRoute(
                navigateToPost = navigateToPostById,
                navigateBack = navigateBack,
                navigateToMission = navigateToMission
            )
        }

    }
}