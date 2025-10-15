package com.virtuous.mypage.graph.swallow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virtuous.designsystem.component.BackButton
import com.virtuous.designsystem.component.getSwallowRes
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.user.SwallowLevel
import com.virtuous.domain.model.user.UserInfo

@Composable
internal fun SwallowRoute(
    navigateBack: () -> Unit,
    viewModel: SwallowViewModel = hiltViewModel()
) {
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    val swallowLevel by viewModel.swallowLevel.collectAsStateWithLifecycle()

    SwallowScreen(
        userInfo = userInfo,
        swallowLevel = swallowLevel,
        navigateBack = navigateBack
    )
}

@Composable
private fun SwallowScreen(
    userInfo: UserInfo,
    swallowLevel: SwallowLevel,
    navigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 55.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(15.dp))

                Image(
                    painter = painterResource(getSwallowRes(swallowLevel)),
                    contentDescription = swallowLevel.label,
                    modifier = Modifier.size(160.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(20.dp))

                Text("현재 제비 레벨 : ${swallowLevel.label}", style = TraceTheme.typography.bodyMM)

                Spacer(Modifier.height(20.dp))

                Text("나의 선행 인증 글 : ${userInfo.verifiedPostCount}개" , style = TraceTheme.typography.bodyMM)

                Spacer(Modifier.height(10.dp))

                Text("나의 미션 인증 : ${userInfo.verifiedPostCount}개" , style = TraceTheme.typography.bodyMM)

                Spacer(Modifier.height(20.dp))

                SwallowLevel.entries.forEach { level ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(getSwallowRes(level)),
                            contentDescription = swallowLevel.label,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        when (level) {
                            SwallowLevel.EGG -> {
                                Text("${level.label} : 첫가입 시 지급", style = TraceTheme.typography.bodySM)
                            }

                            else -> {
                                Text("${level.label} : 선행 인증 글 ${level.verifiedPostCount}개와 미션 인증 ${level.completedMissionCount}개", style = TraceTheme.typography.bodySM)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(60.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(navigateBack)

            Spacer(Modifier.width(10.dp))

            Text("나의 제비", style = TraceTheme.typography.bodyMSB)
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun SwallowScreenPreview() {
    SwallowScreen(
        userInfo = UserInfo("닉네임", null, 0, 0, 0, 0),
        swallowLevel = SwallowLevel.EGG,
        navigateBack = {}
    )
}