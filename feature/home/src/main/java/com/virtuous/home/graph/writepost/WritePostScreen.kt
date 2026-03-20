package com.virtuous.home.graph.writepost

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virtuous.common_ui.compositionlocal.LocalSnackbarHostState
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.BackButton
import com.virtuous.designsystem.component.CheckCancelDialog
import com.virtuous.designsystem.component.ImageContent
import com.virtuous.designsystem.component.TraceContentField
import com.virtuous.designsystem.component.TraceTitleField
import com.virtuous.designsystem.component.VerifyingDialog
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.PrimaryActive
import com.virtuous.designsystem.theme.TextHint
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.WritePostType
import com.virtuous.home.graph.writepost.WritePostViewModel.WritePostEvent
import kotlinx.coroutines.launch


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
    val showVerifyFailureDg by viewModel.showVerifyFailureDialog.collectAsStateWithLifecycle()

    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is WritePostEvent.NavigateToPostDetail -> {
                    navigateToPost(event.postDetail)
                }

                is WritePostEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                is WritePostEvent.NavigateToBack -> navigateBack()
            }
        }
    }

    if (showVerifyFailureDg) {
        CheckCancelDialog(
            onCheck = {
                viewModel.addPost()
            },
            onDismiss = { viewModel.setShowVerifyFailureDialog(false) },
            checkText = stringResource(R.string.register),
            dialogText = stringResource(R.string.verify_fail_dialog_text)
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
    val coroutineScope = rememberCoroutineScope()

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
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(Modifier.width(2.dp))


                        Text(
                            stringResource(R.string.good_deed),
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
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(Modifier.width(2.dp))


                        Text(
                            stringResource(R.string.free),
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
                    onHeightChanged = { diff ->
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(
                                lazyListState.firstVisibleItemIndex,
                                lazyListState.firstVisibleItemScrollOffset + diff
                            )
                        }
                    },
                    hint = if (type == WritePostType.GOOD_DEED) stringResource(R.string.write_content_hint_good_deed) else stringResource(
                        R.string.write_content_hint_free
                    ),
                    modifier = Modifier.focusRequester(contentFieldFocusRequester)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(50.dp)
                .padding(start = 5.dp, end = 20.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(navigateBack, icon = R.drawable.close_ic)

            Spacer(Modifier.width(15.dp))

            Text(
                stringResource(R.string.write_post),
                style = TraceTheme.typography.headingMR
            )

            Spacer(Modifier.weight(1f))

            Text(
                stringResource(R.string.register),
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
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(2.dp))


                    Text(
                        stringResource(R.string.good_deed),
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
            contentDescription = "이미지 첨부하기",
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
                "5장 제한",
                style = TraceTheme.typography.bodySM,
                color = PrimaryActive
            )
        }
    }

}


@Preview(showBackground = true)
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
