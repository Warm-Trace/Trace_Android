package com.example.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.ui.defaultSlideDownFadeOut
import com.example.common.ui.defaultSlideFadeIn
import com.example.common.ui.defaultSlideFadeOut
import com.example.common.ui.defaultSlideUpFadeIn
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.PostFeed
import com.example.home.graph.home.HomeRoute
import com.example.home.graph.post.PostRoute
import com.example.home.graph.search.SearchRoute
import com.example.home.graph.updatepost.UpdatePostRoute
import com.example.home.graph.writepost.WritePostRoute
import com.example.navigation.HomeBaseRoute
import com.example.navigation.HomeGraph

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
            postType = postFeed.postType.name,
            title = postFeed.title,
            isVerified = postFeed.isVerified,
            content = postFeed.content,
            profileImageUrl = postFeed.profileImageUrl,
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
            nickname = postDetail.nickname,
            viewCount = postDetail.viewCount,
            createdAt = postDetail.createdAt.toString()
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

fun NavGraphBuilder.homeNavGraph(
    navigateToSearch: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateToWritePost: () -> Unit,
    navigateToUpdatePost: (Int) -> Unit,
    navigateToPostReplacing: (PostDetail) -> Unit,
    navigateBack: () -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeGraph.HomeRoute) {
        composable<HomeGraph.HomeRoute> {
            HomeRoute(
                navigateToPost = { postFeed -> navigateToPost(postFeed) },
                navigateToWritePost = navigateToWritePost,
                navigateToSearch = navigateToSearch
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
                defaultSlideFadeIn()
            },
            exitTransition = {
                // UpdatePostRoute로 이동할 때만 exitTransition을 null로 설정
                if (targetState.destination.route?.contains("UpdatePostRoute") == true) {
                    null
                } else {
                    defaultSlideFadeOut()
                }
            },
        ) {
            PostRoute(
                navigateBack = navigateBack,
                navigateToUpdatePost = navigateToUpdatePost
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

    }
}

