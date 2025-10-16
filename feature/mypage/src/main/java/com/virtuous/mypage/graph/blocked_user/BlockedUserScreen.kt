package com.virtuous.mypage.graph.blocked_user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virtuous.common_ui.compositionlocal.LocalSnackbarHostState
import com.virtuous.designsystem.component.BackButton
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.user.BlockedUser
import com.virtuous.mypage.graph.blocked_user.BlockedUserViewModel.BlockedUserEvent
import com.virtuous.mypage.graph.blocked_user.component.BlockedUserView
import kotlinx.coroutines.launch
import com.virtuous.designsystem.R

@Composable
fun BlockedUserRoute(
    navigateToUserProfile: (String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: BlockedUserViewModel = hiltViewModel()
) {
    val blockedUsers by viewModel.blockedUsers.collectAsStateWithLifecycle()

    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is BlockedUserEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    BlockedUserScreen(
        blockedUsers = blockedUsers,
        unblockUser = viewModel::unblockUser,
        navigateToUserProfile = navigateToUserProfile,
        navigateBack = navigateBack
    )
}

@Composable
private fun BlockedUserScreen(
    blockedUsers: List<BlockedUser>,
    unblockUser: (String) -> Unit,
    navigateToUserProfile: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 55.dp)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(Modifier.height(10.dp))
            }

            items(blockedUsers.size) { index ->
                blockedUsers[index].let {
                    BlockedUserView(
                        blockedUser = it,
                        navigateToUserProfile = navigateToUserProfile,
                        unblockUser = unblockUser
                    )

                    Spacer(Modifier.height(25.dp))
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(navigateBack)

            Spacer(Modifier.width(10.dp))

            Text(stringResource(R.string.blocked_accounts), style = TraceTheme.typography.bodyMSB)
        }
    }
}

@Preview
@Composable
fun BlockedUserScreenPreview() {
    BlockedUserScreen(
        blockedUsers = listOf(
            BlockedUser(
                providerId = "id_1",
                name = "사용자1",
                profileImageUrl = null,
            ),
            BlockedUser(
                providerId = "id_2",
                name = "사용자2",
                profileImageUrl = "url_2",
            ),
            BlockedUser(
                providerId = "id_3",
                name = "사용자3",
                profileImageUrl = null,
            ),
            BlockedUser(
                providerId = "id_4",
                name = "사용자4",
                profileImageUrl = "url_4",
            ),
            BlockedUser(
                providerId = "id_5",
                name = "사용자5",
                profileImageUrl = null,
            )
        ),
        navigateBack = {},
        navigateToUserProfile = {},
        unblockUser = {}
    )
}