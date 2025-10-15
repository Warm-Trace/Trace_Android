package com.virtuous.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.virtuous.analytics.AnalyticsEvent
import com.virtuous.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.virtuous.analytics.AnalyticsEvent.Types.SCREEN_VIEW
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.auth.navigation.navigateToLogin
import com.virtuous.common_ui.compositionlocal.LocalSnackbarHostState
import com.virtuous.common_ui.ui.TraceBottomBarAnimation
import com.virtuous.designsystem.component.TraceSnackBar
import com.virtuous.designsystem.component.TraceSnackBarHost
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.home.navigation.navigateToHome
import com.virtuous.main.navigation.AppBottomBar
import com.virtuous.main.navigation.AppNavHost
import com.virtuous.navigation.HomeGraph
import com.virtuous.navigation.MissionGraph
import com.virtuous.navigation.getRouteName
import com.virtuous.navigation.shouldHideBottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper
    private lateinit var navController: NavHostController

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        if (intent.extras != null && intent.getStringExtra("type") != null && intent.getStringExtra(
                "id"
            ) != null
        ) { // 백그라운드 알림으로 앱에 진입
            handleNotificationIntent(intent)
        } else {
            lifecycleScope.launch {
                val isSessionValid = viewModel.checkSession()
                if (isSessionValid) {
                    navController.navigateToHome(navOptions {
                        popUpTo(0) {
                            saveState = true
                        }
                        launchSingleTop = true
                    })
                } else {
                    navController.navigateToLogin(navOptions {
                        popUpTo(0) {
                            saveState = true
                        }
                        launchSingleTop = true
                    })
                }
            }

            requestNotificationPermission(this)
        }

        enableEdgeToEdge()


        setContent {
            navController = rememberNavController()
            val currentDestination = navController.currentBackStackEntryAsState()
                .value?.destination
            val snackBarHostState = remember { SnackbarHostState() }

            LifecycleStartEffect(navController) {
                val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                    val screenName = destination.getRouteName()
                    if (screenName != null) {
                        analyticsHelper.logEvent(
                            AnalyticsEvent(
                                type = SCREEN_VIEW,
                                properties = mutableMapOf(SCREEN_NAME to screenName),
                            ),
                        )
                    }
                }

                navController.addOnDestinationChangedListener(listener)

                onStopOrDispose {
                    navController.removeOnDestinationChangedListener(listener)
                }
            }

            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState,
            ) {
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val notificationId = intent.getStringExtra("id") ?: return
        val type = intent.getStringExtra("type") ?: return

        viewModel.readNotification(notificationId)

        when (type) {
            "MISSION" -> {
                navController.navigate(MissionGraph.MissionRoute) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            "COMMENT", "EMOTION" -> {
                intent.getStringExtra("postId")?.toIntOrNull()?.let { postId ->
                    intent.getStringExtra("postId")?.toIntOrNull()?.let { postId ->
                        navController.navigate(HomeGraph.HomeRoute, navOptions {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        })
                        navController.navigate(HomeGraph.PostRoute(postId), navOptions {
                            launchSingleTop = true
                        })
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            ActivityCompat.requestPermissions(activity, arrayOf(permission), 1001)
        }
    }
}

