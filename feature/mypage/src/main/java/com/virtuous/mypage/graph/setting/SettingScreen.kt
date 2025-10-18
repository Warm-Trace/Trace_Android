package com.virtuous.mypage.graph.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.virtuous.common_ui.compositionlocal.LocalSnackbarHostState
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.component.BackButton
import com.virtuous.designsystem.component.CheckCancelDialog
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.Gray
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.mypage.BuildConfig
import com.virtuous.mypage.graph.setting.SettingViewModel.SettingEvent
import kotlinx.coroutines.launch

@Composable
internal fun SettingRoute(
    navigateToWebView: (String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToBlockedUser: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is SettingEvent.NavigateToLogin -> navigateToLogin()
                is SettingEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    SettingScreen(
        navigateToBlockedUser = navigateToBlockedUser,
        navigateToInquiry = { navigateToWebView(BuildConfig.TRACE_INQUIRY_URL) },
        navigateToPrivacyPolicy = { navigateToWebView(BuildConfig.TRACE_PRIVACY_POLICY_URL) },
        navigateBack = navigateBack,
        logout = viewModel::logout,
        unregisterUser = viewModel::unregisterUser
    )
}

@Composable
private fun SettingScreen(
    navigateToBlockedUser: () -> Unit,
    navigateToInquiry: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit,
    navigateBack: () -> Unit,
    logout: () -> Unit,
    unregisterUser: () -> Unit
) {
    var showLogoutDg by remember { mutableStateOf(false) }
    var showUnregisterUserDg by remember { mutableStateOf(false) }

    if (showLogoutDg) {
        CheckCancelDialog(
            onCheck = {
                logout()
                showLogoutDg = false
            },
            onDismiss = { showLogoutDg = false },
            dialogText = stringResource(R.string.sign_out_confirm)
        )
    }

    if (showUnregisterUserDg) {
        CheckCancelDialog(
            onCheck = {
                unregisterUser()
                showUnregisterUserDg = false
            },
            onDismiss = { showUnregisterUserDg = false },
            dialogText = stringResource(R.string.unregister_confirm)
        )
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 55.dp, start = 20.dp, end = 20.dp)
        ) {
            item {
                Spacer(Modifier.height(15.dp))

                Text(
                    stringResource(R.string.guide),
                    style = TraceTheme.typography.bodyMSB
                )

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.app_version),
                        style = TraceTheme.typography.bodyMR
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        stringResource(R.string.app_version_value),
                        color = Gray,
                        style = TraceTheme.typography.bodyMR
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToInquiry()
                        }) {
                    Text(
                        stringResource(R.string.contact_us),
                        style = TraceTheme.typography.bodyMR
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToPrivacyPolicy()
                        }) {
                    Text(
                        stringResource(R.string.privacy_policy),
                        style = TraceTheme.typography.bodyMR
                    )
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = GrayLine
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    stringResource(R.string.account),
                    style = TraceTheme.typography.bodyMSB
                )

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToBlockedUser()
                        }) {
                    Text(
                        stringResource(R.string.blocked_accounts),
                        style = TraceTheme.typography.bodyMR
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showLogoutDg = true
                        }) {
                    Text(
                        stringResource(R.string.sign_out),
                        style = TraceTheme.typography.bodyMR
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showUnregisterUserDg = true
                        }) {
                    Text(
                        stringResource(R.string.unregister),
                        style = TraceTheme.typography.bodyMR
                    )
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = GrayLine
                )
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(navigateBack)

            Spacer(Modifier.width(10.dp))

            Text(stringResource(R.string.setting), style = TraceTheme.typography.bodyMSB)
        }
    }
}


@Preview
@Composable
fun SettingScreenPreview() {
    SettingScreen(
        navigateToBlockedUser = {},
        navigateBack = {},
        navigateToInquiry = {},
        navigateToPrivacyPolicy = {},
        logout = {},
        unregisterUser = {},
    )
}