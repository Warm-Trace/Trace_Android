package com.example.home.graph.writepost

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.event.TraceEvent
import com.example.common.util.clickable
import com.example.designsystem.R
import com.example.designsystem.component.CheckCancelDialog
import com.example.designsystem.component.ImageContent
import com.example.designsystem.component.TraceContentField
import com.example.designsystem.component.TraceTitleField
import com.example.designsystem.component.VerifyingDialog
import com.example.designsystem.theme.Background
import com.example.designsystem.theme.Black
import com.example.designsystem.theme.GrayLine
import com.example.designsystem.theme.PrimaryActive
import com.example.designsystem.theme.TextHint
import com.example.designsystem.theme.TraceTheme
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.WritePostType
import com.example.home.graph.writepost.WritePostViewModel.WritePostEvent


@Composable
internal fun WritePostRoute(
    navigateBack: () -> Unit,
    navigateToPost: (PostDetail) -> Unit,
    viewModel: WritePostViewModel = hiltViewModel(),
) {
    val type by viewModel.type.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val images by viewModel.images.collectAsStateWithLifecycle()
    val isVerified by viewModel.isVerified.collectAsStateWithLifecycle()
    val isCreatingPost by viewModel.isCreatingPost.collectAsStateWithLifecycle()
    val isVerifyingPost by viewModel.isVerifyingPost.collectAsStateWithLifecycle()

    var showVerifyFailDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is WritePostEvent.AddPostSuccess -> {
                    navigateToPost(event.postDetail)
                    viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("게시글이 등록되었습니다."))
                }

                is WritePostEvent.AddPostFailure -> {
                    viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("게시글 등록에 실패했습니다."))
                }

                is WritePostEvent.VerifyFailure -> {
                    showVerifyFailDialog = true
                }

                is WritePostEvent.NavigateToBack -> navigateBack()
            }
        }
    }

    if (showVerifyFailDialog) {
        CheckCancelDialog(
            onCheck = {
                viewModel.addPost()
            },
            onDismiss = { showVerifyFailDialog = false },
            checkText = "등록",
            dialogText = "게시글이 선행 인증에 실패했습니다.\n그래도 등록하시겠습니까?"
        )
    }

    WritePostScreen(
        type = type,
        title = title,
        content = content,
        images = images,
        isVerified = isVerified,
        isCreatingPost = isCreatingPost,
        isVerifyingPost = isVerifyingPost,
        onTitleChange = viewModel::setTitle,
        onContentChange = viewModel::setContent,
        addImages = viewModel::addImages,
        removeImage = viewModel::removeImage,
        onTypeChange = viewModel::setType,
        onIsVerifiedChange = viewModel::setIsVerified,
        addPost = viewModel::addPost,
        verifyAndAddPost = viewModel::verifyAndAddPost,
        navigateBack = navigateBack,
    )
}

@Composable
private fun WritePostScreen(
    type: WritePostType,
    title: String,
    content: String,
    images: List<String>,
    isVerified: Boolean,
    isCreatingPost: Boolean,
    isVerifyingPost: Boolean,
    onTypeChange: (WritePostType) -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    addImages: (List<String>) -> Unit,
    removeImage: (String) -> Unit,
    onIsVerifiedChange: (Boolean) -> Unit,
    addPost: () -> Unit,
    verifyAndAddPost: () -> Unit,
    navigateBack: () -> Unit,
) {
    val contentFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val lazyListState = rememberLazyListState()

    val requestAvailable by remember(title, content) {
        derivedStateOf { title.isNotEmpty() && content.isNotEmpty() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(vertical = 70.dp, horizontal = 15.dp)
                .imePadding(),
            state = lazyListState
        ) {

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.clickable(isRipple = true) {
                            onTypeChange(WritePostType.GOOD_DEED)
                        }
                    ) {
                        Image(
                            painter = if (type == WritePostType.GOOD_DEED) painterResource(R.drawable.checkbox_on) else painterResource(
                                R.drawable.checkbox_off
                            ),
                            contentDescription = "선행 게시글 타입",
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(Modifier.width(2.dp))


                        Text(
                            "선행",
                            color = if (type == WritePostType.GOOD_DEED) PrimaryActive else TextHint,
                            style = TraceTheme.typography.bodySSB,
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    Row(
                        modifier = Modifier.clickable(isRipple = true) {
                            onTypeChange(WritePostType.FREE)
                        }
                    ) {
                        Image(
                            painter = if (type == WritePostType.FREE) painterResource(R.drawable.checkbox_on) else painterResource(
                                R.drawable.checkbox_off
                            ),
                            contentDescription = "선행 게시글 타입",
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(Modifier.width(2.dp))


                        Text(
                            "자유",
                            color = if (type == WritePostType.FREE) PrimaryActive else TextHint,
                            style = TraceTheme.typography.bodySSB,
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                TraceTitleField(
                    value = title,
                    onValueChange = onTitleChange,
                    onNext = { contentFieldFocusRequester.requestFocus() }
                )

                Spacer(Modifier.height(15.dp))

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayLine)
                )

                Spacer(Modifier.height(15.dp))

                if (images.isNotEmpty()) ImageContent(images, removeImage)

                TraceContentField(
                    value = content,
                    onValueChange = onContentChange,
                    lazyListState = lazyListState,
                    hint = if (type == WritePostType.GOOD_DEED) "따뜻한 흔적을 남겨보세요!" else "내용을 입력하세요.",
                    modifier = Modifier.focusRequester(contentFieldFocusRequester)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(50.dp)
                .padding(horizontal = 15.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "뒤로 가기",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        isRipple = true
                    ) {
                        navigateBack()
                    }
            )

            Spacer(Modifier.width(30.dp))

            Text("글 쓰기", style = TraceTheme.typography.headingMR)

            Spacer(Modifier.weight(1f))

            Text(
                "완료",
                style = TraceTheme.typography.bodyMM,
                color = if (requestAvailable) PrimaryActive else TextHint,
                modifier = Modifier.clickable(isRipple = true, enabled = requestAvailable) {
                    keyboardController?.hide()
                    if (type == WritePostType.GOOD_DEED && isVerified) verifyAndAddPost() else addPost()
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 5.dp, horizontal = 15.dp)
                .align(Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically
        ) {
            GalleryPicker(imagesSize = images.size, addImages = addImages)

            Spacer(Modifier.weight(1f))

            if (type == WritePostType.GOOD_DEED) {
                Row(
                    modifier = Modifier.clickable(isRipple = true) {
                        onIsVerifiedChange(!isVerified)
                    }
                ) {
                    Image(
                        painter = if (isVerified) painterResource(R.drawable.checkbox_on) else painterResource(
                            R.drawable.checkbox_off
                        ),
                        contentDescription = "선행 인증",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(2.dp))


                    Text(
                        "선행 인증",
                        color = if (isVerified) PrimaryActive else PrimaryActive.copy(alpha = 0.7f),
                        style = TraceTheme.typography.bodySSB,
                    )
                }
            }


        }

        if (isCreatingPost) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black.copy(alpha = 0.1f))
                    .align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    color = PrimaryActive,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (isVerifyingPost) {
            VerifyingDialog()
        }
    }
}

@Composable
private fun GalleryPicker(
    imagesSize: Int,
    maxSelection: Int = 5,
    addImages: (List<String>) -> Unit,
) {
    val remaining = maxSelection - imagesSize

    val multipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxSelection.coerceAtLeast(2)
        )
    ) { uris: List<Uri> ->
        addImages(uris.map { it.toString() })
    }

    val singleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { addImages(listOf(it.toString())) }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.add_image_ic),
            contentDescription = "사진 첨부",
            tint = PrimaryActive,
            modifier = Modifier
                .size(32.dp)
                .clickable(enabled = remaining > 0) {
                    if (remaining >= 2) {
                        multipleLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } else if (remaining == 1) {
                        singleLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                }
        )

        if (remaining == 0) {
            Spacer(Modifier.width(4.dp))

            Text(
                "5장 제한", style = TraceTheme.typography.bodySM,
                color = PrimaryActive
            )
        }
    }

}


@Preview
@Composable
fun WritePostScreenPreview() {
    WritePostScreen(
        type = WritePostType.GOOD_DEED,
        title = "",
        content = "",
        isVerified = true,
        isCreatingPost = false,
        isVerifyingPost = false,
        onTypeChange = {},
        onTitleChange = {},
        onContentChange = {},
        onIsVerifiedChange = {},
        navigateBack = {},
        images = emptyList(),
        addImages = {},
        removeImage = {},
        addPost = {},
        verifyAndAddPost = {}
    )
}