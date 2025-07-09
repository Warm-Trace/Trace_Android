package com.example.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.ui.defaultSlideDownFadeOut
import com.example.common.ui.defaultSlideUpFadeIn
import com.example.mission.graph.mission.MissionRoute
import com.example.mission.graph.verifymission.VerifyMissionRoute
import com.example.navigation.MissionBaseRoute
import com.example.navigation.MissionGraph

fun NavController.navigateToMission(navOptions: NavOptions? = null) {
    navigate(MissionGraph.MissionRoute)
}

fun NavController.navigateToVerifyMission(description: String, navOptions: NavOptions? = null) {
    navigate(MissionGraph.VerifyMissionRoute(description))
}


fun NavGraphBuilder.missionNavGraph(
    navigateBack: () -> Unit,
    navigateToPost: (Int) -> Unit,
    navigateToVerifyMission: (String) -> Unit,
) {
    navigation<MissionBaseRoute>(startDestination = MissionGraph.MissionRoute) {
        composable<MissionGraph.MissionRoute> {
            MissionRoute(
                navigateToPost = navigateToPost,
                navigateToVerifyMission = navigateToVerifyMission
            )
        }

        composable<MissionGraph.VerifyMissionRoute>(
            enterTransition = { defaultSlideUpFadeIn() },
            exitTransition = { defaultSlideDownFadeOut() }
        ) {
            VerifyMissionRoute(
                navigateToPost = navigateToPost,
                navigateBack = navigateBack,
            )
        }
    }
}