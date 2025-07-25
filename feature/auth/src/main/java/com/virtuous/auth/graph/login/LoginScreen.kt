package com.virtuous.auth.graph.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.virtuous.auth.graph.login.LoginViewModel.LoginEvent
import com.virtuous.common.event.TraceEvent
import com.virtuous.common.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.TraceTheme


@Composable
internal fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToEditProfile: (String, String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> navigateToHome()
                is LoginEvent.NavigateEditProfile -> navigateToEditProfile(
                    event.signUpToken,
                    event.providerId
                )
            }
        }
    }

    LoginScreen(
        viewModel::loginKakao,
        onLoginFailure = { viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("로그인에 실패했습니다")) },
    )
}

@Composable
private fun LoginScreen(
    loginKakao: (String) -> Unit,
    onLoginFailure: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(4f))

        Image(
            painter = painterResource(R.drawable.app_title),
            contentDescription = "앱 타이틀",
        )

        Spacer(Modifier.height(50.dp))

        Text("작은 선행,", style = TraceTheme.typography.appDescription)

        Text("따뜻한 흔적을 남기다", style = TraceTheme.typography.appDescription)

        Spacer(Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.kakao_login),
            contentDescription = "카카오 로그인",
            modifier = Modifier.clickable {
                loginKakao(context, loginKakao, onLoginFailure)
            }
        )

        Spacer(Modifier.weight(5f))

    }
}

private fun loginKakao(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onFailure()
        } else if (token != null) {
            onSuccess(token.idToken!!)
        }
    }

    UserApiClient.instance.apply {
        if (isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    loginWithKakaoAccount(context, callback = callback)
                } else if (token?.idToken != null) {
                    onSuccess(token.idToken!!)
                    Log.d("idToken", token.idToken!!)
                }
            }
        } else {
            loginWithKakaoAccount(context, callback = callback)
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        loginKakao = {},
        onLoginFailure = {},
    )
}