package com.virtuous.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.White


@Composable
fun ImageContent(
    images: List<String>,
    removeImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(0) { images.size }
    var expanded by remember { mutableStateOf(false) }

    if (images.isNotEmpty()) {
        Spacer(Modifier.height(10.dp))

        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxWidth()
        ) { pageIndex ->
            val imagePainter = rememberAsyncImagePainter(images[pageIndex])

            Box(
                modifier = modifier.fillMaxWidth(),
            ) {
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 3.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(12.dp)),
                ) {
                    Image(
                        painter = imagePainter,
                        contentDescription = "Image $pageIndex",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (pageIndex == 0) {
                    Box(
                        modifier = modifier
                            .align(Alignment.TopStart)
                            .padding(top = 12.dp, start = 12.dp)
                            .background(PrimaryDefault, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("대표", style = TraceTheme.typography.bodySSB, color = White)
                    }
                }

                Box(
                    modifier = modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(35.dp)
                        .background(Black.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${pageIndex + 1} / ${images.size}",
                        color = White,
                        style = TraceTheme.typography.bodySM.copy(fontSize = 12.sp)
                    )
                }

                Box(
                    modifier = modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(30.dp)
                        .background(Black.copy(0.8f), shape = CircleShape)
                        .clickable {
                            expanded = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "이미지 삭제",
                        tint = White,
                        modifier = modifier.size(20.dp)
                    )

                    Box(

                    ) {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = modifier
                                .background(White, shape = RoundedCornerShape(8.dp))
                        ) {

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "사진 삭제하기",
                                        style = TraceTheme.typography.bodySM.copy(fontSize = 12.sp),
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    removeImage(images[pageIndex])
                                },
                            )

                        }
                    }
                }
            }
        }


        Spacer(modifier.height(15.dp))
    }
}