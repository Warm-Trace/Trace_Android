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
    MissionGraph.VerifyMissionRoute::class,
    MyPageGraph.UpdateProfileRoute::class,
    MyPageGraph.SettingRoute::class,
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



