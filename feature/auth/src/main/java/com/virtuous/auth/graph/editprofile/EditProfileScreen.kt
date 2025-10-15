package com.virtuous.auth.graph.editprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virtuous.auth.graph.editprofile.EditProfileViewModel.EditProfileEvent
import com.virtuous.common_ui.compositionlocal.LocalSnackbarHostState
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.BackButton
import com.virtuous.designsystem.component.ProfileImage
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.PrimaryActive
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.Red
import com.virtuous.designsystem.theme.TextField
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.White
import kotlinx.coroutines.launch

@Composable
internal fun EditProfileRoute(
    navigateToHome: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isNameValid by viewModel.isNameValid.collectAsStateWithLifecycle()
    val profileImageUrl by viewModel.profileImage.collectAsStateWithLifecycle()

    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is EditProfileEvent.NavigateToHome -> {
                    navigateToHome()
                }

                is EditProfileEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
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
        listOf(
            stringResource(R.string.pick_from_album,),
           stringResource( R.string.apply_default_image)
        )
    } else {
        listOf(stringResource(R.string.pick_from_album))
    }

    val signUpAvailability by remember(name, isNameValid) {
        derivedStateOf {
            name.isNotEmpty() && isNameValid
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, start = 30.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                ProfileImage(
                    profileImageUrl = profileImageUrl,
                    size = 115
                )

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
                                            style = TraceTheme.typography.bodyXSM,
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        when (option) {
                                            "사진/앨범에서 불러오기" -> imageLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )

                                            "기본 이미지 적용" -> onProfileImageUrlChange(
                                                null
                                            )
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
                stringResource(R.string.user_name),
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
                        stringResource(R.string.enter_user_name),
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
                    stringResource(R.string.nickname_validation_message),
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
                    stringResource(R.string.complete),
                    color = White,
                    style = TraceTheme.typography.bodyXMM
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(navigateBack)

            Spacer(Modifier.width(10.dp))

            Text(
                stringResource(R.string.profile_setup),
                style = TraceTheme.typography.bodyMSB
            )
        }
    }
}


@Preview
@Composable
private fun EditProfileScreenPreview() {
    TraceTheme {
        EditProfileScreen(
            name = "홍길동",
            profileImageUrl = null,
            isNameValid = true,
            onNameChange = {},
            onProfileImageUrlChange = {},
            registerUser = {},
            navigateBack = {}
        )
    }
}

