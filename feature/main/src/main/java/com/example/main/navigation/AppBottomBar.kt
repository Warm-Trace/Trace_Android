package com.example.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.example.common.ui.NoRippleInteractionSource
import com.example.designsystem.theme.Black
import com.example.designsystem.theme.PrimaryDefault
import com.example.designsystem.theme.TraceTheme
import com.example.designsystem.theme.White
import com.example.navigation.HomeBaseRoute
import com.example.navigation.MissionBaseRoute
import com.example.navigation.MyPageBaseRoute
import com.example.navigation.Route
import com.example.navigation.isRouteInHierarchy

@Composable
internal fun AppBottomBar(
    currentDestination: NavDestination?,
    navigateToBottomNaviDestination: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .height(51.dp)
            .drawBehind {
                val shadowHeight = 1.dp.toPx()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Black.copy(alpha = 0.1f),
                        ),
                        startY = 0f,
                        endY = shadowHeight
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = Size(size.width, shadowHeight)
                )
            }
    ) {
        NavigationBar(
            containerColor = White,
            modifier = modifier
                .align(Alignment.BottomCenter)
                .height(50.dp)
        ) {
            TopLevelDestination.entries.forEach { topLevelRoute ->

                NavigationBarItem(
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 2.dp),
                        ) {

                            Icon(
                                painter = painterResource(topLevelRoute.unSelectedIcon),
                                contentDescription = topLevelRoute.contentDescription,
                                modifier = Modifier.size(24.dp),
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text = topLevelRoute.title,
                                style = TraceTheme.typography.bodyXSM
                            )
                        }
                    },
                    onClick = {
                        when (topLevelRoute) {
                            TopLevelDestination.HOME -> navigateToBottomNaviDestination(HomeBaseRoute)
                            TopLevelDestination.MISSION -> navigateToBottomNaviDestination(MissionBaseRoute)
                            TopLevelDestination.MY_Page -> navigateToBottomNaviDestination(MyPageBaseRoute)
                        }
                    },
                    selected = currentDestination.isRouteInHierarchy(topLevelRoute.route),
                    interactionSource = remember { NoRippleInteractionSource() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryDefault,
                        selectedTextColor = PrimaryDefault,
                        unselectedTextColor = Black,
                        indicatorColor = Color.Transparent
                    ),

                    )
            }
        }

    }
}