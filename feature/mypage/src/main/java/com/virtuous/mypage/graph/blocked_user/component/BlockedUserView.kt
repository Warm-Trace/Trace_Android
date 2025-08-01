package com.virtuous.mypage.graph.blocked_user.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.component.CheckCancelDialog
import com.virtuous.designsystem.component.ProfileImage
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.user.BlockedUser
import java.time.LocalDateTime

@Composable
internal fun BlockedUserView(
    blockedUser: BlockedUser,
    unblockUser: (String) -> Unit,
    navigateToUserProfile: (String) -> Unit,
) {
    var showUnblockUserDg by remember { mutableStateOf(false) }

    if (showUnblockUserDg) {
        CheckCancelDialog(
            onCheck = {
                unblockUser(blockedUser.providerId)
                showUnblockUserDg = false
            },
            onDismiss = { showUnblockUserDg = false },
            title = "${blockedUser.name}님을 차단 해제하시겠습니까?",
            dialogText = "이제 ${blockedUser.name}님의 모든 콘텐츠가 다시 표시되고, 관련 알림 수신이 재개됩니다"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToUserProfile(blockedUser.providerId)
            },
        verticalAlignment = Alignment.CenterVertically
    )
    {
        ProfileImage(
            profileImageUrl = blockedUser.profileImageUrl,
            imageSize = if (blockedUser.profileImageUrl != null) 40.dp else 36.dp,
            paddingValue = if (blockedUser.profileImageUrl != null) 1.dp else 3.dp,
        )

        Spacer(Modifier.width(12.dp))

        Text(blockedUser.name, style = TraceTheme.typography.bodySSB)

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = Black,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { showUnblockUserDg = true }
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "차단 해제",
                style = TraceTheme.typography.bodyXMSB,
                color = Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BlockedUserViewPreview() {
    TraceTheme {
        BlockedUserView(
            blockedUser = BlockedUser(
                providerId = "preview_user",
                name = "차단된 사용자",
                profileImageUrl = null,
                blockedAt = LocalDateTime.now()
            ),
            unblockUser = {},
            navigateToUserProfile = {}
        )
    }
}