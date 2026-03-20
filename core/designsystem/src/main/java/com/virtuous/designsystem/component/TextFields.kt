package com.virtuous.designsystem.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TextHint
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.post.PostRule

private val customTextSelectionColors = TextSelectionColors(
    handleColor = PrimaryDefault,
    backgroundColor = PrimaryDefault.copy(
        alpha = 0.75f
    )
)

@Composable
fun TraceTitleField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit,
    hint: String = "제목",
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int = PostRule.MAX_TITLE_LENGTH
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    keyboardController?.hide()
                    onNext()
                }
            ),
            textStyle = TraceTheme.typography.bodyMSB,
            cursorBrush = SolidColor(PrimaryDefault),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = TraceTheme.typography.bodyMSB,
                        color = TextHint,
                    )
                }

                innerTextField()
            },
            modifier = modifier
        )
    }

}

@Composable
fun TraceContentField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onHeightChanged: (Int) -> Unit,
    hint: String = "",
    maxLength: Int = PostRule.MAX_CONTENT_LENGTH
) {
    var prevHeight by remember { mutableIntStateOf(0) }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            textStyle = TraceTheme.typography.bodyMM,
            cursorBrush = SolidColor(PrimaryDefault),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = TraceTheme.typography.bodyMM,
                        color = TextHint,
                    )

                }

                innerTextField()
            },
            modifier = modifier
                .fillMaxWidth()
                .onSizeChanged {
                    val diff = it.height - prevHeight
                    prevHeight = it.height
                    if (prevHeight == 0 || diff == 0) {
                        return@onSizeChanged
                    }

                    onHeightChanged(diff)
                }
        )
    }
}
