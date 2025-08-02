package com.virtuous.mypage.graph.blocked_user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.user.BlockedUser
import com.virtuous.mypage.graph.blocked_user.component.BlockedUserView

@Composable
fun BlockedUserRoute(
    navigateBack: () -> Unit,
    viewModel: BlockedUserViewModel = hiltViewModel()
) {
    val blockedUsers by viewModel.blockedUsers.collectAsStateWithLifecycle()

    BlockedUserScreen(
        blockedUsers = blockedUsers,
        navigateBack = navigateBack
    )
}

@Composable
private fun BlockedUserScreen(
    blockedUsers: List<BlockedUser>,
    navigateBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(Modifier.height(68.dp))
            }

            items(blockedUsers.size) { index ->
                blockedUsers[index].let {
                    BlockedUserView(blockedUser = it, navigateToUserProfile = {}, unblockUser = {})

                    Spacer(Modifier.height(25.dp))
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier
                    .padding(start = 10.dp, top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(Modifier.width(10.dp))

            Text("차단된 계정", style = TraceTheme.typography.bodyMSB)
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
        navigateBack = {}
    )
}