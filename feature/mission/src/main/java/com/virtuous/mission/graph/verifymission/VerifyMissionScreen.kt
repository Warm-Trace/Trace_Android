package com.virtuous.mission.graph.verifymission


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.virtuous.common_ui.event.TraceEvent
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.ImageContent
import com.virtuous.designsystem.component.TraceContentField
import com.virtuous.designsystem.component.TraceTitleField
import com.virtuous.designsystem.component.VerifyingDialog
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.PrimaryActive
import com.virtuous.designsystem.theme.TextHint
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.mission.graph.verifymission.VerifyMissionViewModel.VerifyMissionEvent
import com.virtuous.mission.graph.verifymission.component.VerifyMissionHeaderView


@Composable
internal fun VerifyMissionRoute(
    navigateBack: () -> Unit,
    navigateToPost: (Int) -> Unit,
    viewModel: VerifyMissionViewModel = hiltViewModel(),
) {
    val description = viewModel.description

    val title by viewModel.title.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val images by viewModel.images.collectAsStateWithLifecycle()
    val isVerifyingMission by viewModel.isVerifyingMission.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is VerifyMissionEvent.VerifyMissionSuccess -> {
                    viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("미션 인증에 성공했습니다!"))
                    navigateToPost(event.postId)
                }

                is VerifyMissionEvent.VerifyMissionFailure -> {
                    viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("미션 인증에 실패했습니다."))
                }

                is VerifyMissionEvent.NavigateToBack -> navigateBack()
            }
        }
    }

    VerifyMissionScreen(
        description = description,
        title = title,
        content = content,
        images = images,
        isVerifyingMission = isVerifyingMission,
        onTitleChange = viewModel::setTitle,
        onContentChange = viewModel::setContent,
        addImages = viewModel::addImages,
        removeImage = viewModel::removeImage,
        verifyMission = viewModel::verifyMission,
        navigateBack = navigateBack,
    )
}

@Composable
private fun VerifyMissionScreen(
    description: String,
    title: String,
    content: String,
    images: List<String>,
    isVerifyingMission: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    addImages: (List<String>) -> Unit,
    removeImage: (String) -> Unit,
    verifyMission : () -> Unit,
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
                VerifyMissionHeaderView(description)

                Spacer(Modifier.height(12.dp))

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
                    hint = "미션에 도전해보세요!",
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

            Spacer(Modifier.width(26.dp))

            Text("미션 인증하기", style = TraceTheme.typography.headingMR)

            Spacer(Modifier.weight(1f))

            Text(
                "완료",
                style = TraceTheme.typography.bodyMM,
                color = if (requestAvailable) PrimaryActive else TextHint,
                modifier = Modifier.clickable(isRipple = true, enabled = requestAvailable) {
                    keyboardController?.hide()
                    verifyMission()
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
        }

        if (isVerifyingMission) {
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
fun VerifyMissionScreenPreview() {
    VerifyMissionScreen(
        title = "",
        content = "",
        isVerifyingMission = false,
        images = emptyList(), description = "길거리에서 쓰레기 줍기",
        onContentChange = {},
        addImages = {},
        removeImage = {},
        onTitleChange = {},
        verifyMission = {},
        navigateBack = {},
    )
}