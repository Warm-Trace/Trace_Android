package com.virtuous.home.graph.post

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.common.event.TraceEvent
import com.virtuous.common.util.clickable
import com.virtuous.common.util.formatCount
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.ProfileImage
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.DarkGray
import com.virtuous.designsystem.theme.EmotionLabel
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.MissionBackground
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.Red
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.WarmGray
import com.virtuous.designsystem.theme.White
import com.virtuous.domain.model.post.Comment
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.model.post.EmotionCount
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.PostType
import com.virtuous.home.graph.post.PostViewModel.PostEvent
import com.virtuous.home.graph.post.component.CommentView
import com.virtuous.home.graph.post.component.OtherPostDropdownMenu
import com.virtuous.home.graph.post.component.OwnPostDropdownMenu
import com.virtuous.home.graph.post.component.PostImageContent
import com.virtuous.home.graph.post.component.TraceCommentField
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@Composable
internal fun PostRoute(
    navigateBack: () -> Unit,
    navigateToUpdatePost: (Int) -> Unit,
    viewModel: PostViewModel = hiltViewModel()
) {
    val comments = viewModel.commentPagingFlow.collectAsLazyPagingItems()
    val commentInput by viewModel.commentInput.collectAsStateWithLifecycle()
    val postDetail by viewModel.postDetail.collectAsStateWithLifecycle()
    val replyTargetId by viewModel.replyTargetId.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is PostEvent.DeletePostSuccess -> {
                    navigateBack()
                    viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("게시글이 삭제되었습니다."))
                }

                is PostEvent.DeletePostFailure -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "게시글 삭제에 실패했습니다."
                    )
                )

                is PostEvent.ReportPostSuccess,
                is PostEvent.ReportCommentSuccess -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "신고가 접수되었습니다."
                    )
                )

                is PostEvent.ReportPostFailure -> {}
                is PostEvent.ReportCommentFailure -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "신고 접수에 실패했습니다."
                    )
                )

                is PostEvent.AddCommentSuccess -> {}
                is PostEvent.AddCommentFailure -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "댓글 작성에 실패했습니다."
                    )
                )

                is PostEvent.AddReplySuccess -> {}
                is PostEvent.AddReplyFailure -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "답글 작성에 실패했습니다."
                    )
                )

                is PostEvent.DeleteCommentSuccess -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "댓글이 삭제되었습니다."
                    )
                )

                is PostEvent.DeleteCommentFailure -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        "댓글 삭제에 실패했습니다."
                    )
                )

                is PostEvent.ShowSnackBar -> viewModel.eventHelper.sendEvent(
                    TraceEvent.ShowSnackBar(
                        event.message
                    )
                )
            }
        }
    }

    PostScreen(
        postDetail = postDetail,
        comments = comments,
        commentInput = commentInput,
        isReplying = replyTargetId != null,
        replyTargetId = replyTargetId,
        onCommentInputChange = viewModel::setCommentInput,
        onAddComment = viewModel::addComment,
        onDeletePost = viewModel::deletePost,
        onReportPost = viewModel::reportPost,
        toggleEmotion = viewModel::toggleEmotion,
        onDeleteComment = viewModel::deleteComment,
        onReplyComment = viewModel::replyComment,
        onReplyTargetIdChange = viewModel::setReplyTargetId,
        clearReplayTargetId = viewModel::clearReplyTargetId,
        onReportComment = viewModel::reportComment,
        onBlockUser = viewModel::blockUser,
        navigateBack = navigateBack,
        navigateToUpdatePost = navigateToUpdatePost,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PostScreen(
    postDetail: PostDetail,
    comments: LazyPagingItems<Comment>,
    commentInput: String,
    isReplying: Boolean,
    replyTargetId: Int?,
    onDeletePost: () -> Unit,
    onReportPost: (String) -> Unit,
    toggleEmotion: (Emotion) -> Unit,
    onAddComment: () -> Unit,
    onCommentInputChange: (String) -> Unit,
    onDeleteComment: (Int) -> Unit,
    onReplyComment: ((Int) -> Unit) -> Unit,
    onReplyTargetIdChange: (Int) -> Unit,
    clearReplayTargetId: () -> Unit,
    onReportComment: (Int, String) -> Unit,
    onBlockUser: (String) -> Unit,
    navigateToUpdatePost: (Int) -> Unit,
    navigateBack: () -> Unit,
) {
    var isOwnPostDropDownMenuExpanded by remember { mutableStateOf(false) }
    var isOtherPostDropDownMenuExpanded by remember { mutableStateOf(false) }


    val isRefreshing = comments.loadState.refresh is LoadState.Loading
    val isAppending = comments.loadState.append is LoadState.Loading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing, onRefresh = { comments.refresh() })

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val scrollOffset = with(LocalDensity.current) { -200.dp.toPx().toInt() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding()
            .pullRefresh(pullRefreshState)
    ) {
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            contentColor = PrimaryDefault,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 45.dp, start = 20.dp, end = 20.dp, bottom = 50.dp)
        ) {

            item {
                Spacer(Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(postDetail.title, style = TraceTheme.typography.bodyLSB)

                    if (postDetail.isVerified) {
                        Spacer(Modifier.width(8.dp))

                        Image(
                            painter = painterResource(R.drawable.verification_mark),
                            contentDescription = "선행 인증 마크",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }


                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileImage(
                        profileImageUrl = postDetail.profileImageUrl,
                        imageSize = if (postDetail.profileImageUrl != null) 38.dp else 34.dp,
                        paddingValue = if (postDetail.profileImageUrl != null) 1.dp else 3.dp,
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(postDetail.nickname, style = TraceTheme.typography.bodySSB)

                        Spacer(Modifier.height(3.dp))

                        Row() {
                            Text(
                                postDetail.getFormattedDate(),
                                style = TraceTheme.typography.bodyXSM,
                                color = DarkGray
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                "${postDetail.viewCount} 읽음",
                                style = TraceTheme.typography.bodyXSM,
                                color = DarkGray
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = GrayLine
                )

                postDetail.missionContent?.let {
                    Spacer(Modifier.height(15.dp))

                    MissionHeader(it)
                }

                if (postDetail.images.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    PostImageContent(images = postDetail.images)
                }

                Spacer(Modifier.height(15.dp))

                Text(
                    postDetail.content,
                    style = TraceTheme.typography.bodyMR.copy(fontSize = 15.sp, lineHeight = 19.sp)
                )

                Spacer(Modifier.height(50.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Emotion.entries.forEach { emotion ->
                        val emotionCount = when (emotion) {
                            Emotion.HEARTWARMING -> postDetail.emotionCount.heartWarmingCount
                            Emotion.LIKEABLE -> postDetail.emotionCount.likeableCount
                            Emotion.TOUCHING -> postDetail.emotionCount.touchingCount
                            Emotion.IMPRESSIVE -> postDetail.emotionCount.impressiveCount
                            Emotion.GRATEFUL -> postDetail.emotionCount.gratefulCount
                        }

                        val emotionResource = when (emotion) {
                            Emotion.HEARTWARMING -> R.drawable.heartwarming
                            Emotion.LIKEABLE -> R.drawable.likeable
                            Emotion.TOUCHING -> R.drawable.touching
                            Emotion.IMPRESSIVE -> R.drawable.impressive
                            Emotion.GRATEFUL -> R.drawable.grateful
                        }

                        val isBorder = emotion == postDetail.yourEmotionType

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(onClick = {
                                toggleEmotion(emotion)
                            }, modifier = Modifier.then(Modifier.size(36.dp))) {
                                Box(
                                    modifier = Modifier.size(28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(emotionResource),
                                        contentDescription = emotion.label,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    if (isBorder) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .border(1.dp, PrimaryDefault, CircleShape)
                                        )
                                    }

                                    if (emotion == Emotion.GRATEFUL) {
                                        Icon(
                                            painter = painterResource(R.drawable.heart),
                                            tint = Red,
                                            contentDescription = "하트",
                                            modifier = Modifier
                                                .size(5.5.dp)
                                                .align(Alignment.CenterEnd)
                                                .offset(x = 1.dp)
                                        )

                                        Icon(
                                            painter = painterResource(R.drawable.heart),
                                            tint = Red,
                                            contentDescription = "하트",
                                            modifier = Modifier
                                                .size(5.5.dp)
                                                .align(Alignment.CenterStart)
                                                .offset(x = -2.dp)
                                        )

                                        Icon(
                                            painter = painterResource(R.drawable.heart),
                                            tint = Red,
                                            contentDescription = "하트",
                                            modifier = Modifier
                                                .size(5.5.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = -3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(3.dp))

                            Text(
                                emotion.label,
                                style = TraceTheme.typography.bodySR,
                                color = if (emotion == postDetail.yourEmotionType) PrimaryDefault else EmotionLabel
                            )

                            Spacer(Modifier.height(3.dp))

                            Text(
                                emotionCount.formatCount(),
                                style = TraceTheme.typography.bodySSB,
                                color = if (emotion == postDetail.yourEmotionType) PrimaryDefault else Black
                            )
                        }
                    }


                }

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = GrayLine
                )

                if (comments.itemCount == 0 && comments.loadState.refresh is LoadState.NotLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(55.dp))

                        Text(
                            "아직 댓글이 없습니다.",
                            style = TraceTheme.typography.bodyMM,
                            color = Black,
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "당신의 생각을 댓글로 남겨주세요.",
                            style = TraceTheme.typography.bodySM,
                            color = WarmGray
                        )
                    }

                }

            }

            items(
                comments.itemCount
            ) { index ->
                comments[index]?.let { comment ->
                    if (!comment.isDeleted || comment.replies.isNotEmpty()) {

                        Spacer(Modifier.height(13.dp))

                        CommentView(
                            comment = comment,
                            replyTargetId = replyTargetId,
                            onDelete = onDeleteComment,
                            onReport = { commentId, reason -> onReportComment(commentId, reason) },
                            onReply = {
                                onReplyTargetIdChange(comment.commentId)

                                coroutineScope.launch {
                                    focusRequester.requestFocus()
                                    keyboardController?.show()

                                    listState.animateScrollToItem(
                                        index = index + 1, scrollOffset = scrollOffset
                                    )
                                }
                            })

                        if (index != comments.itemCount - 1) {
                            Spacer(Modifier.height(15.dp))

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = GrayLine
                            )
                        }
                    }
                }
            }


            item {
                Spacer(Modifier.height(300.dp))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    PrimaryDefault
                )
                .padding(horizontal = 20.dp)
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.arrow_back_white_ic),
                contentDescription = "뒤로 가기",
                modifier = Modifier.clickable {
                    navigateBack()
                })

            Spacer(Modifier.width(20.dp))

            Text(postDetail.postType.label, style = TraceTheme.typography.headingMB, color = White)

            Spacer(Modifier.weight(1f))

            Box() {
                Image(
                    painter = painterResource(R.drawable.menu_ic),
                    contentDescription = "메뉴",
                    modifier = Modifier.clickable(isRipple = true) {
                        if (postDetail.isOwner) {
                            isOwnPostDropDownMenuExpanded = true
                        } else {
                            isOtherPostDropDownMenuExpanded = true
                        }
                    })

                OwnPostDropdownMenu(
                    expanded = isOwnPostDropDownMenuExpanded,
                    onDismiss = { isOwnPostDropDownMenuExpanded = false },
                    onRefresh = { comments.refresh() },
                    onUpdate = { navigateToUpdatePost(postDetail.postId) },
                    onDelete = onDeletePost,
                )

                OtherPostDropdownMenu(
                    expanded = isOtherPostDropDownMenuExpanded,
                    onDismiss = { isOtherPostDropDownMenuExpanded = false },
                    onRefresh = { comments.refresh() },
                    onReport = onReportPost,
                    onBlockUser = { onBlockUser(postDetail.providerId) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .background(Background)
                .align(Alignment.BottomCenter),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TraceCommentField(
                value = commentInput,
                focusRequester = focusRequester,
                isReplying = isReplying,
                onValueChange = onCommentInputChange,
                onAddComment = {
                    keyboardController?.hide()

                    onAddComment()

                    coroutineScope.launch {
                        val visibleItems = listState.layoutInfo.visibleItemsInfo
                        val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
                        val lastItemIndex = comments.itemCount

                        if (lastVisibleIndex < lastItemIndex) {
                            listState.animateScrollToItem(index = lastItemIndex)
                        }
                    }
                },
                onReplyComment = {
                    onReplyComment({ commentId ->
                        coroutineScope.launch {
                            val targetIndex = (0 until comments.itemCount).firstOrNull { index ->
                                comments[index]?.commentId == commentId
                            } ?: -1

                            if (targetIndex != -1) {
                                listState.animateScrollToItem(index = targetIndex)
                            }
                        }
                    })

                    keyboardController?.hide()
                },
                clearReplyTargetId = clearReplayTargetId
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isRefreshing || isAppending) {
            CircularProgressIndicator(
                color = PrimaryDefault, modifier = Modifier.align(
                    if (isRefreshing) Alignment.Center else Alignment.BottomCenter
                )
            )
        }
    }
}

@Composable
fun MissionHeader(missionContent: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(MissionBackground)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(
                    color = com.virtuous.designsystem.theme.MissionHeader,
                    radius = size.minDimension / 2f
                )
            }

            Spacer(Modifier.width(4.dp))

            Text(
                "일일 미션",
                style = TraceTheme.typography.missionHeaderSmall,
                color = com.virtuous.designsystem.theme.MissionHeader,
            )

            Spacer(Modifier.width(4.dp))

            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(
                    color = com.virtuous.designsystem.theme.MissionHeader,
                    radius = size.minDimension / 2f
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            missionContent,
            style = TraceTheme.typography.missionTitleSmall,
            color = Black,
        )

        Spacer(Modifier.height(15.dp))
    }
}

@Preview
@Composable
fun PostScreenPreview() {
    val fakePostDetail = PostDetail(
        postId = 0,
        providerId = "1234",
        postType = PostType.GOOD_DEED,
        title = "작은 선행을 나누다",
        content = "오늘은 작은 선행을 나누었습니다. 많은 사람들에게 도움이 되었으면 좋겠습니다.",
        nickname = "홍길동",
        viewCount = 120,
        emotionCount = EmotionCount(
            heartWarmingCount = 35,
            likeableCount = 50,
            touchingCount = 15,
            impressiveCount = 20,
            gratefulCount = 10
        ),
        images = listOf(
            "https://picsum.photos/200/300?random=1",
            "https://picsum.photos/200/300?random=2",
            "https://picsum.photos/200/300?random=3"
        ),
        profileImageUrl = "https://picsum.photos/200/300?random=1",
        createdAt = LocalDateTime.now().minusDays(3),
        updatedAt = LocalDateTime.now(),
        isVerified = true,
        isOwner = true,

        )


    PostScreen(
        commentInput = "",
        postDetail = fakePostDetail,
        comments = fakeLazyPagingComments(),
        isReplying = true,
        onCommentInputChange = {},
        navigateBack = {},
        navigateToUpdatePost = {},
        onAddComment = {},
        onReplyComment = { },
        onDeleteComment = {},
        onDeletePost = {},
        onReportComment = { _, _ -> },
        onReportPost = {},
        onReplyTargetIdChange = {},
        clearReplayTargetId = {},
        toggleEmotion = {},
        onBlockUser = {},
        replyTargetId = null
    )
}

@Composable
fun fakeLazyPagingComments(): LazyPagingItems<Comment> {
    val fakeChildComments = listOf(
        Comment(
            nickName = "이수지",
            profileImageUrl = "https://randomuser.me/api/portraits/women/3.jpg",
            content = "정말 좋은 내용이에요!",
            createdAt = LocalDateTime.now().minusMinutes(30), providerId = "1234", postId = 1,
            commentId = 11, parentId = 1, isOwner = true,
        ), Comment(
            nickName = "박영희",
            profileImageUrl = null,
            content = "완전 공감해요!",
            createdAt = LocalDateTime.now().minusDays(2), providerId = "1234", postId = 1,
            commentId = 12, parentId = 1, isOwner = true,
        ), Comment(
            nickName = "최민준",
            profileImageUrl = null,
            content = "읽기만 했는데 좋네요!",
            createdAt = LocalDateTime.now().minusHours(10), providerId = "1234", postId = 1,
            commentId = 13, parentId = 1, isOwner = true,
        )
    )

    return flowOf(
        PagingData.from(
            listOf(
                Comment(
                    nickName = "홍길동",
                    profileImageUrl = "https://randomuser.me/api/portraits/men/1.jpg",
                    content = "이 글 정말 감동적이에요!",
                    createdAt = LocalDateTime.now().minusDays(1),
                    providerId = "1234",
                    postId = 1,
                    commentId = 14,
                    parentId = null,
                    isOwner = true,
                    replies = fakeChildComments
                ), Comment(
                    nickName = "김민수",
                    profileImageUrl = "https://randomuser.me/api/portraits/men/2.jpg",
                    content = "좋은 글 감사합니다!",
                    createdAt = LocalDateTime.now().minusHours(5), providerId = "1234", postId = 1,
                    commentId = 24, parentId = null, isOwner = true,
                ), Comment(
                    nickName = "이수지",
                    profileImageUrl = "https://randomuser.me/api/portraits/women/3.jpg",
                    content = "정말 좋은 내용이에요!",
                    createdAt = LocalDateTime.now().minusMinutes(30),
                    providerId = "1234",
                    postId = 1,
                    commentId = 34,
                    parentId = null,
                    isOwner = true,
                ), Comment(
                    nickName = "박영희",
                    profileImageUrl = null,
                    content = "완전 공감해요!",
                    createdAt = LocalDateTime.now().minusDays(2), providerId = "1234", postId = 1,
                    commentId = 44, parentId = null, isOwner = true,
                ), Comment(
                    nickName = "최민준",
                    profileImageUrl = null,
                    content = "읽기만 했는데 좋네요!",
                    createdAt = LocalDateTime.now().minusHours(10), providerId = "1234", postId = 1,
                    commentId = 54, parentId = null, isOwner = true,
                )
            )
        )
    ).collectAsLazyPagingItems()
}

