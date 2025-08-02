package com.virtuous.main.navigation

import androidx.annotation.DrawableRes
import com.virtuous.designsystem.R
import com.virtuous.navigation.HomeBaseRoute
import com.virtuous.navigation.MissionBaseRoute
import com.virtuous.navigation.MyPageBaseRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route : KClass<*>,
    @DrawableRes val icon : Int,
    val contentDescription: String,
    val title : String
) {
    HOME(
        route = HomeBaseRoute::class,
        icon = R.drawable.home_actvie,
        contentDescription = "홈",
        title = "홈"
    ),
    MISSION(
        route = MissionBaseRoute::class,
        icon = R.drawable.mission_active,
        contentDescription = "미션",
        title = "미션"
    ),
    MY_Page(
        route = MyPageBaseRoute::class,
        icon = R.drawable.my_page_active,
        contentDescription = "마이페이지",
        title = "마이페이지"
    ),
}