package com.virtuous.home.graph.post.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtuous.common.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.CommentField
import com.virtuous.designsystem.theme.Gray
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme


private val customTextSelectionColors = TextSelectionColors(
    handleColor = PrimaryDefault,
    backgroundColor = PrimaryDefault.copy(
        alpha = 0.75f
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TraceCommentField(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    value: String,
    isReplying: Boolean = false,
    onValueChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onReplyComment: () -> Unit,
    clearReplyTargetId: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val isKeyboardVisible = WindowInsets.isImeVisible
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            clearReplyTargetId()
        }
    }

    val hint = if (isReplying && isKeyboardVisible) "답글을 입력하세요." else "댓글을 입력하세요."

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Default
            ),
            textStyle = TraceTheme.typography.bodySM.copy(fontSize = 15.sp),
            cursorBrush = SolidColor(PrimaryDefault),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 13.dp)
                        .fillMaxWidth(),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = TraceTheme.typography.bodySM,
                            color = Gray,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        innerTextField()

                        Spacer(Modifier.weight(1f))

                        Image(
                            painter = painterResource(R.drawable.send_ic),
                            contentDescription = "댓글 작성",
                            modifier = Modifier.clickable {
                                if (isReplying) onReplyComment() else onAddComment()
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            },
            modifier = modifier
                .onFocusChanged {
                    isFocused = it.isFocused
                }
                .focusRequester(focusRequester)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(CommentField)
                .padding(start = 14.dp, end = 10.dp)
                .heightIn(max = 150.dp)
        )
    }
}