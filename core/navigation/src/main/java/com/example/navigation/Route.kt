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
    data class EditProfileRoute(val signUpToken : String, val providerId: String) : AuthGraph()
}

@Serializable
data object HomeBaseRoute : Route

sealed class HomeGraph : Route {
    @Serializable
    data object HomeRoute : HomeGraph()

    @Serializable
    data object PostRoute :  HomeGraph()

    @Serializable
    data object WritePostRoute :  HomeGraph()
}


@Serializable
data object MissionBaseRoute : Route

sealed class MissionGraph : Route {
    @Serializable
    data object MissionRoute : MissionGraph()
}

@Serializable
data object MyPageBaseRoute : Route

sealed class  MyPageGraph : Route {
    @Serializable
    data object MyPageRoute : MyPageGraph()

    @Serializable
    data object UpdateProfileRoute : MyPageGraph()

    @Serializable
    data object SettingRoute : MyPageGraph()
}




