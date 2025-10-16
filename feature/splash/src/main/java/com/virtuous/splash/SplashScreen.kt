package com.virtuous.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.Background

@Composable
internal fun SplashRoute(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                SplashViewModel.SplashEvent.NavigateToHome -> navigateToHome()
                SplashViewModel.SplashEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.9f))

        Image(painter = painterResource(R.drawable.ic_splash), contentDescription = "스플래시 아이콘")

        Spacer(Modifier.weight(1f))
    }
}


@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}