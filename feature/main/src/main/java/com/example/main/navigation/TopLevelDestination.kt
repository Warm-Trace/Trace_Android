package com.example.main.navigation

import androidx.annotation.DrawableRes
import com.example.designsystem.R
import com.example.navigation.HomeGraph
import com.example.navigation.MissionGraph
import com.example.navigation.MyPageGraph
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route : KClass<*>,
    @DrawableRes val icon : Int,
    val contentDescription: String,
    val title : String
) {
    HOME(
        route = HomeGraph.HomeRoute::class,
        icon = R.drawable.home_actvie,
        contentDescription = "홈",
        title = "홈"
    ),
    MISSION(
        route = MissionGraph.MissionRoute::class,
        icon = R.drawable.mission_active,
        contentDescription = "미션",
        title = "미션"
    ),
    MY_Page(
        route = MyPageGraph.MyPageRoute::class,
        icon = R.drawable.my_page_active,
        contentDescription = "마이페이지",
        title = "마이페이지"
    ),
}