package com.example.navigation

import kotlinx.serialization.Serializable


sealed interface Route

@Serializable
data object SplashRoute : Route

@Serializable
data object AuthGraphBaseRoute : Route

sealed class AuthGraph : Route {
    @Serializable
    data object LoginRoute : AuthGraph()

    @Serializable
    data class EditProfileRoute(val signUpToken: String, val providerId: String) : AuthGraph()
}

@Serializable
data object HomeBaseRoute : Route

sealed class HomeGraph : Route {
    @Serializable
    data object HomeRoute : HomeGraph()

    @Serializable
    data object SearchRoute : HomeGraph()

    @Serializable
    data class PostRoute(
        val postId: Int,
        val postType: String = "",
        val title: String = "",
        val isVerified: Boolean = false,
        val content: String = "",
        val profileImageUrl: String? = null,
        val imageUrl: String? = null,
        val nickname: String = "",
        val viewCount: Int = 0,
        val createdAt: String = "",
        val missionContent: String? = null,
        val providerId: String = "",
        val isOwner: Boolean = false,
        val yourEmotionType: String? = null,
    ) : HomeGraph()

    @Serializable
    data object WritePostRoute : HomeGraph()

    @Serializable
    data class UpdatePostRoute(val postId: Int) : HomeGraph()
}


@Serializable
data object MissionBaseRoute : Route

sealed class MissionGraph : Route {
    @Serializable
    data object MissionRoute : MissionGraph()

    @Serializable
    data class VerifyMissionRoute(val description: String) : MissionGraph()
}

@Serializable
data object MyPageBaseRoute : Route

sealed class MyPageGraph : Route {
    @Serializable
    data object MyPageRoute : MyPageGraph()

    @Serializable
    data object UpdateProfileRoute : MyPageGraph()

    @Serializable
    data object SettingRoute : MyPageGraph()

    @Serializable
    data class WebViewRoute(val url: String) : MyPageGraph()
}




