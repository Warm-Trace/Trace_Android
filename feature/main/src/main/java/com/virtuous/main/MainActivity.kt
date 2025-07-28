package com.virtuous.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.virtuous.common_ui.ui.TraceBottomBarAnimation
import com.virtuous.designsystem.component.TraceSnackBar
import com.virtuous.designsystem.component.TraceSnackBarHost
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.main.navigation.AppBottomBar
import com.virtuous.main.navigation.AppNavHost
import com.virtuous.navigation.HomeGraph
import com.virtuous.navigation.MissionGraph
import com.virtuous.navigation.NavigationEvent
import com.virtuous.navigation.NavigationHelper
import com.virtuous.navigation.shouldHideBottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        if (intent.extras != null) { // 백그라운드 알림으로 앱에 진입
            handleNotificationIntent(intent, viewModel.navigationHelper)
        } else {
            viewModel.checkSession()
            requestNotificationPermission(this)
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val currentDestination = navController.currentBackStackEntryAsState()
                .value?.destination
            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, {
                        launch {
                            viewModel.navigationHelper.navigationFlow.collect { event ->
                                handleNavigationEvent(
                                    navController = navController,
                                    event = event,
                                )
                            }
                        }

                        launch {
                            viewModel.eventHelper.eventChannel.collect { event ->
                                when (event) {
                                    is com.virtuous.common_ui.event.TraceEvent.ShowSnackBar -> snackBarHostState.showSnackbar(
                                        event.message
                                    )
                                }
                            }
                        }
                    })
                }

            }

            TraceTheme {
                val imeIsShown = WindowInsets.isImeVisible
                val statusBarPadding =
                    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                val bottomPadding =
                    if (imeIsShown) 0.dp else WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(
                        top = statusBarPadding,
                        bottom = bottomPadding
                    ),
                    snackbarHost = {
                        TraceSnackBarHost(
                            hostState = snackBarHostState,
                            snackbar = { snackBarData -> TraceSnackBar(snackBarData) }
                        )
                    },
                    containerColor = Background,
                    bottomBar = {
                        TraceBottomBarAnimation(
                            visible = currentDestination?.shouldHideBottomBar() == false,
                            modifier = Modifier.navigationBarsPadding(),
                        ) {
                            AppBottomBar(
                                currentDestination = currentDestination,
                                navigateToBottomNaviDestination = { bottomNaviDestination ->
                                    navController.navigate(
                                        bottomNaviDestination,
                                        navOptions = navOptions {
                                            popUpTo(0) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        })
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent, viewModel.navigationHelper)
    }

    private fun handleNavigationEvent(
        navController: NavController,
        event: NavigationEvent
    ) {
        when (event) {
            is NavigationEvent.To -> {
                val navOptions = navOptions {
                    if (event.popUpTo) {
                        popUpTo(0) { inclusive = true }
                    }

                    launchSingleTop = true
                }

                navController.navigate(
                    route = event.route,
                    navOptions = navOptions
                )
            }

            is NavigationEvent.Up -> navController.navigateUp()

            is NavigationEvent.StackAndTo -> {
                navController.navigate(event.stackRoute, navOptions = navOptions {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                })
                navController.navigate(event.toRoute, navOptions = navOptions {
                    launchSingleTop = true
                })
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent, navigationHelper: NavigationHelper) {
        val type = intent.getStringExtra("type") ?: return

        when (type) {
            "mission" -> {
                navigationHelper.navigate(
                    NavigationEvent.To(MissionGraph.MissionRoute, popUpTo = true)
                )
            }

            "comment", "emotion" -> {
                intent.getStringExtra("postId")?.toIntOrNull()?.let { postId ->
                    navigationHelper.navigate(
                        NavigationEvent.StackAndTo(
                            stackRoute = HomeGraph.HomeRoute,
                            toRoute = HomeGraph.PostRoute(postId)
                        )
                    )
                }
            }
        }
    }

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 1001)
            }

        }
    }
}
