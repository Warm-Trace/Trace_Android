package com.example.home.graph.post.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.util.clickable
import com.example.designsystem.R
import com.example.designsystem.component.ProfileImage
import com.example.designsystem.theme.Background
import com.example.designsystem.theme.DarkGray
import com.example.designsystem.theme.Gray
import com.example.designsystem.theme.GrayLine
import com.example.designsystem.theme.PrimaryDefault
import com.example.designsystem.theme.TraceTheme
import com.example.designsystem.theme.WarmGray
import com.example.domain.model.post.Comment
import java.time.LocalDateTime

@Composable
internal fun CommentView(
    comment: Comment,
    replyTargetId: Int?,
    onDelete: (Int) -> Unit,
    onReply: () -> Unit,
    onReport: (Int, String) -> Unit,
) {
    var isOwnCommentDropDownMenuExpanded by remember { mutableStateOf(false) }
    var isOtherCommentDropDownMenuExpanded by remember { mutableStateOf(false) }

    val backgroundColor =
        if (replyTargetId != null && replyTargetId.equals(comment.commentId)) PrimaryDefault.copy(
            alpha = 0.2f
        ) else Background

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        if (!comment.isDeleted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    profileImageUrl = comment.profileImageUrl,
                    imageSize = if (comment.profileImageUrl != null) 23.dp else 21.dp,
                    paddingValue = if (comment.profileImageUrl != null) 1.dp else 2.dp
                )

                Spacer(Modifier.width(6.dp))

                Text(comment.nickName, style = TraceTheme.typography.bodySSB)

                Spacer(Modifier.width(6.dp))

                Text(
                    comment.getFormattedTime(),
                    style = TraceTheme.typography.bodyXSM,
                    color = DarkGray
                )

                Spacer(Modifier.weight(1f))

                Box() {
                    Image(
                        painter = painterResource(R.drawable.menu_ic),
                        contentDescription = "댓글 메뉴",
                        colorFilter = ColorFilter.tint(WarmGray),
                        modifier = Modifier
                            .height(15.dp)
                            .clickable {
                                if (comment.isOwner) {
                                    isOwnCommentDropDownMenuExpanded = true
                                } else {
                                    isOtherCommentDropDownMenuExpanded = true
                                }
                            }
                    )

                    OwnCommentDropdownMenu(
                        expanded = isOwnCommentDropDownMenuExpanded,
                        onDismiss = { isOwnCommentDropDownMenuExpanded = false },
                        onReply = onReply,
                        onDelete = { onDelete(comment.commentId) },
                    )

                    OtherCommentDropdownMenu(
                        expanded = isOtherCommentDropDownMenuExpanded,
                        onDismiss = { isOtherCommentDropDownMenuExpanded = false },
                        onReply = onReply,
                        onReport = { reason -> onReport(comment.commentId, reason) },
                    )
                }
            }

            Spacer(Modifier.height(7.dp))

            Text(comment.content, style = TraceTheme.typography.bodyMR)

            Spacer(Modifier.height(5.dp))

            Text(
                "답글 달기",
                style = TraceTheme.typography.bodySM.copy(
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                color = DarkGray,
                modifier = Modifier.clickable {
                    onReply()
                })
        }

        if (comment.isDeleted) {
            Text("삭제된 댓글입니다.", style = TraceTheme.typography.bodySM, color = Gray)
        }
    }

    comment.replies.filter { !it.isDeleted }.forEachIndexed { index, childComment ->
        if (index == 0) Spacer(Modifier.height(20.dp))

        ChildCommentView(
            childComment,
            onDelete = onDelete,
            onReport = onReport,
        )

        if (index != comment.replies.size - 1) Spacer(Modifier.height(20.dp))
    }

}

@Composable
private fun ChildCommentView(
    comment: Comment,
    onDelete: (Int) -> Unit,
    onReport: (Int, String) -> Unit
) {
    var isOwnCommentDropDownMenuExpanded by remember { mutableStateOf(false) }
    var isOtherCommentDropDownMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(
                profileImageUrl = comment.profileImageUrl,
                imageSize = if (comment.profileImageUrl != null) 23.dp else 19.dp,
                paddingValue = if (comment.profileImageUrl != null) 1.dp else 3.dp
            )

            Spacer(Modifier.width(6.dp))

            Text(comment.nickName, style = TraceTheme.typography.bodySSB)

            Spacer(Modifier.width(6.dp))

            Text(
                comment.getFormattedTime(),
                style = TraceTheme.typography.bodyXSM,
                color = DarkGray
            )

            Spacer(Modifier.weight(1f))

            Box() {
                Image(
                    painter = painterResource(R.drawable.menu_ic),
                    contentDescription = "댓글 메뉴",
                    colorFilter = ColorFilter.tint(WarmGray),
                    modifier = Modifier
                        .height(15.dp)
                        .clickable {
                            if (comment.isOwner) {
                                isOwnCommentDropDownMenuExpanded = true
                            } else {
                                isOtherCommentDropDownMenuExpanded = true
                            }
                        }
                )

                OwnChildCommentDropdownMenu(
                    expanded = isOwnCommentDropDownMenuExpanded,
                    onDismiss = { isOwnCommentDropDownMenuExpanded = false },
                    onDelete = { onDelete(comment.commentId) },
                )

                OtherChildCommentDropdownMenu(
                    expanded = isOtherCommentDropDownMenuExpanded,
                    onDismiss = { isOtherCommentDropDownMenuExpanded = false },
                    onReport = { reason -> onReport(comment.commentId, reason) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(comment.content, style = TraceTheme.typography.bodyMR)
    }

}

val fakeChildComments = listOf(
    Comment(
        nickName = "이수지",
        profileImageUrl = "https://randomuser.me/api/portraits/women/3.jpg",
        content = "정말 좋은 내용이에요!",
        createdAt = LocalDateTime.now().minusMinutes(30), providerId = "1234", postId = 1,
        commentId = 1, parentId = 1, isOwner = true,
    ),
    Comment(
        nickName = "박영희",
        profileImageUrl = null,
        content = "완전 공감해요!",
        createdAt = LocalDateTime.now().minusDays(2), providerId = "1234", postId = 1,
        commentId = 1, parentId = 1, isOwner = true,
    ),
    Comment(
        nickName = "최민준",
        profileImageUrl = null,
        content = "읽기만 했는데 좋네요!",
        createdAt = LocalDateTime.now().minusHours(10), providerId = "1234", postId = 1,
        commentId = 1, parentId = 1, isOwner = true,
    )
)

@Preview
@Composable
private fun CommentViewPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        CommentView(
            comment = Comment(
                nickName = "홍길동",
                profileImageUrl = "https://randomuser.me/api/portraits/men/1.jpg",
                content = "이 글 정말 감동적이에요!",
                createdAt = LocalDateTime.now().minusDays(1),
                providerId = "1234", postId = 1,
                commentId = 1, parentId = 1, isOwner = true, replies = fakeChildComments
            ),
            onReply = { },
            onReport = { _, _ -> },
            onDelete = { _ -> },
            replyTargetId = null
        )

        Spacer(Modifier.height(11.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GrayLine)
        )
    }


}
