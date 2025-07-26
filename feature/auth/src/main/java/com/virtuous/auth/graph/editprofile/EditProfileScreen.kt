package com.virtuous.auth.graph.editprofile


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.virtuous.auth.graph.editprofile.EditProfileViewModel.EditProfileEvent
import com.virtuous.common_ui.event.TraceEvent
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.PrimaryActive
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.Red
import com.virtuous.designsystem.theme.TextField
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.White


@Composable
internal fun EditProfileRoute(
    navigateToHome: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isNameValid by viewModel.isNameValid.collectAsStateWithLifecycle()
    val profileImageUrl by viewModel.profileImage.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is EditProfileEvent.RegisterUserSuccess -> {
                    navigateToHome()
                }

                is EditProfileEvent.RegisterUserFailure -> {
                    viewModel.eventHelper.sendEvent(TraceEvent.ShowSnackBar("프로필 설정에 실패했습니다"))
                }
            }
        }
    }

    EditProfileScreen(
        navigateBack = navigateBack,
        name = name,
        isNameValid = isNameValid,
        profileImageUrl = profileImageUrl,
        onNameChange = viewModel::setName,
        onProfileImageUrlChange = viewModel::setProfileImageUrl,
        registerUser = viewModel::registerUser,
    )

}


@Composable
private fun EditProfileScreen(
    name: String,
    profileImageUrl: String?,
    isNameValid: Boolean,
    onNameChange: (String) -> Unit,
    onProfileImageUrlChange: (String?) -> Unit,
    registerUser: () -> Unit,
    navigateBack: () -> Unit
) {

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                onProfileImageUrlChange(uri.toString())
            }
        }
    )

    var expanded by remember { mutableStateOf(false) }

    val options = if (profileImageUrl != null) {
        listOf("사진/앨범에서 불러오기", "기본 이미지 적용")
    } else {
        listOf("사진/앨범에서 불러오기")
    }

    val signUpAvailability by remember(name, isNameValid) {
        derivedStateOf {
            name.isNotEmpty() && isNameValid
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로 가기",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Start)
                .padding(8.dp)
                .clickable(isRipple = true) {
                    navigateBack()
                }
        )


        Spacer(Modifier.height(25.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = PrimaryDefault)) {
                    append("프로필")
                }
                append("설정")
            },
            style = TraceTheme.typography.headingLB,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp)
        )

        Spacer(Modifier.height(26.dp))

        Box() {
            Canvas(modifier = Modifier.size(133.dp)) {
                val canvasWidth = size.width
                val center = center
                val radius = canvasWidth / 2f

                drawCircle(
                    color = PrimaryDefault,
                    radius = radius,
                    center = center,
                    style = Stroke(12f)
                )
            }

            ProfileImage(profileImageUrl)

            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.camera_ic),
                    contentDescription = "프로필 이미지 설정",
                    modifier = Modifier
                        .clickable {
                            expanded = true
                        }
                )

                Box() {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(White, shape = RoundedCornerShape(8.dp))
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        style = TraceTheme.typography.bodySM.copy(fontSize = 12.sp),
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    when (option) {
                                        "사진/앨범에서 불러오기" -> imageLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )

                                        "기본 이미지 적용" -> onProfileImageUrlChange(null)
                                    }
                                },
                            )
                        }
                    }
                }

            }
        }

        Spacer(Modifier.height(46.dp))

        Text(
            "사용자 이름",
            style = TraceTheme.typography.headingMB,
            color = PrimaryDefault,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            placeholder = {
                Text(
                    "사용자 이름을 입력해주세요",
                    style = TraceTheme.typography.bodySM,
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            maxLines = 1,
            textStyle = TextStyle(fontSize = 14.sp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = TextField,
                focusedContainerColor = TextField,
                focusedIndicatorColor = PrimaryActive,
                unfocusedIndicatorColor = PrimaryDefault,
                cursorColor = PrimaryDefault
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
        )

        if (name.isNotEmpty() && !isNameValid) {
            Spacer(Modifier.height(2.dp))

            Text(
                "닉네임은 최소 2자, 최대 12자까지 가능해요",
                style = TraceTheme.typography.bodyXSM.copy(fontSize = 12.sp),
                color = Red,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 10.dp, start = 20.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (signUpAvailability) registerUser()
            },
            colors = if (!signUpAvailability) ButtonDefaults.buttonColors(
                PrimaryDefault
                    .copy(alpha = 0.65F)
            ) else ButtonDefaults.buttonColors(
                PrimaryActive
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                "완료",
                color = White,
                style = TraceTheme.typography.bodyXMM
            )
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun ProfileImage(imageUrl: String?) {
    val profileImage = rememberAsyncImagePainter(imageUrl ?: R.drawable.default_profile)
    val imageSize = if (imageUrl != null) 129.dp else 115.dp
    val paddingValue = if (imageUrl != null) 2.dp else 9.dp

    Box(Modifier.padding(paddingValue)) {
        Image(
            painter = profileImage,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}


