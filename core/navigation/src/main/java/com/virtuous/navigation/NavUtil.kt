package com.virtuous.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val HIDDEN_BOTTOM_BAR_ROUTES = setOf(
    AuthGraph.LoginRoute::class,
    AuthGraph.EditProfileRoute::class,
    HomeGraph.WritePostRoute::class,
    HomeGraph.PostRoute::class,
    HomeGraph.SearchRoute::class,
    HomeGraph.UpdatePostRoute::class,
    HomeGraph.UserProfileRoute::class,
    HomeGraph.NotificationRoute::class,
    MissionGraph.VerifyMissionRoute::class,
    MyPageGraph.UpdateProfileRoute::class,
    MyPageGraph.WebViewRoute::class,
    SplashRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean =
    this?.route?.let { route ->
        HIDDEN_BOTTOM_BAR_ROUTES.any { hiddenRoute ->
            route.startsWith(hiddenRoute.qualifiedName ?: "")
        }
    } ?: false

fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

fun NavDestination?.containsRoute(routes: List<KClass<*>>): Boolean {
    val currentRoute = this?.route ?: return false
    return routes.mapNotNull { it.simpleName }.any { currentRoute.contains(it) }
}

fun NavDestination.getRouteName(): String? = this.route?.let { mapRouteToName(it) }

private fun mapRouteToName(route: String): String? = when {
    route.startsWith(AuthGraph.LoginRoute::class.qualifiedName.orEmpty()) -> "login"
    route.startsWith(AuthGraph.EditProfileRoute::class.qualifiedName.orEmpty()) -> "edit_profile"
    route.startsWith(HomeGraph.HomeRoute::class.qualifiedName.orEmpty()) -> "home"
    route.startsWith(HomeGraph.SearchRoute::class.qualifiedName.orEmpty()) -> "search"
    route.startsWith(HomeGraph.PostRoute::class.qualifiedName.orEmpty()) -> "post"
    route.startsWith(HomeGraph.WritePostRoute::class.qualifiedName.orEmpty()) -> "write_post"
    route.startsWith(HomeGraph.UpdatePostRoute::class.qualifiedName.orEmpty()) -> "update_post"
    route.startsWith(HomeGraph.UserProfileRoute::class.qualifiedName.orEmpty()) -> "user_profile"
    route.startsWith(HomeGraph.NotificationRoute::class.qualifiedName.orEmpty()) -> "notification"
    route.startsWith(MissionGraph.MissionRoute::class.qualifiedName.orEmpty()) -> "mission"
    route.startsWith(MissionGraph.VerifyMissionRoute::class.qualifiedName.orEmpty()) -> "verify_mission"
    route.startsWith(MyPageGraph.UpdateProfileRoute::class.qualifiedName.orEmpty()) -> "update_profile"
    route.startsWith(MyPageGraph.BlockedUserRoute::class.qualifiedName.orEmpty()) -> "blocked_user"
    route.startsWith(MyPageGraph.SwallowRoute::class.qualifiedName.orEmpty()) -> "swallow"
    route.startsWith(MyPageGraph.MyPageRoute::class.qualifiedName.orEmpty()) -> "my_page"
    route.startsWith(MyPageGraph.SettingRoute::class.qualifiedName.orEmpty()) -> "setting"
    route.startsWith(MyPageGraph.WebViewRoute::class.qualifiedName.orEmpty()) -> "webview"
    route.startsWith(SplashRoute::class.qualifiedName.orEmpty()) -> "splash"
    else -> null
}
