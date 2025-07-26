package com.virtuous.home.graph.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.SearchField
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.WarmGray

private const val MIN_SEARCH_LENGTH = 2

private val customTextSelectionColors = TextSelectionColors(
    handleColor = PrimaryDefault,
    backgroundColor = PrimaryDefault.copy(
        alpha = 0.75f
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TraceSearchField(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    keyboardType: KeyboardType = KeyboardType.Text,
    value: String,
    hint: String = "검색어를 입력하세요.",
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    resetSearch: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            resetSearch()
        }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch()
                if (value.length >= MIN_SEARCH_LENGTH) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }),
            textStyle = TraceTheme.typography.bodySM,
            cursorBrush = SolidColor(PrimaryDefault),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = TraceTheme.typography.bodySM,
                            color = WarmGray,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        innerTextField()

                        if (value.isNotEmpty()) {
                            Spacer(Modifier.weight(1f))

                            Image(
                                painter = painterResource(R.drawable.remove_keyword_ic),
                                contentDescription = "검색어 초기화",
                                modifier = Modifier.clickable {
                                    onValueChange("")
                                })
                        }
                    }
                }
            },
            modifier = modifier
                .onFocusChanged {
                    isFocused = it.isFocused
                }
                .focusRequester(focusRequester)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(SearchField)
                .padding(start = 14.dp, end = 10.dp)
                .height(35.dp),
        )
    }
}