package com.virtuous.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.virtuous.designsystem.R


val PretendardBold = FontFamily(
    Font(
        resId = R.font.pretendard_bold,
        weight = FontWeight.Bold
    )
)

val PretendardSemiBold = FontFamily(
    Font(
        resId = R.font.pretendard_semi_bold,
        weight = FontWeight.SemiBold
    )
)

val PretendardMedium = FontFamily(
    Font(
        resId = R.font.pretendard_medium,
        weight = FontWeight.Medium
    )
)
val PretendardRegular = FontFamily(
    Font(
        resId = R.font.pretendard_regular,
        weight = FontWeight.Normal
    )
)

val PretendardLight = FontFamily(
    Font(
        resId = R.font.pretendard_light,
        weight = FontWeight.Light
    )
)

val HsJipTokkiRound = FontFamily(
    Font(
        resId = R.font.hs_jiptokki_round,
    )
)

val HsGoolTokkiRegular = FontFamily(
    Font(
        resId = R.font.hs_gooltokki,
        weight = FontWeight.Normal
    )
)


@Immutable
data class TraceTypography(
    val headingXLB: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 50.sp,
        lineHeight = 60.sp
    ),
    val headingLB: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    val headingMB: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val headingMR: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val missionVerification: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val missionHeader: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    val missionHeaderSmall: TextStyle = TextStyle(
        fontFamily = HsJipTokkiRound,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val missionTitle: TextStyle = TextStyle(
        fontFamily = HsGoolTokkiRegular,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    val missionTitleSmall: TextStyle = TextStyle(
        fontFamily = HsGoolTokkiRegular,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val appDescription: TextStyle = TextStyle(
        fontFamily = HsGoolTokkiRegular,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),
    val missionCompletedTitle: TextStyle = TextStyle(
        fontFamily = HsGoolTokkiRegular,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),
    val myPageTab: TextStyle = TextStyle(
        fontFamily = HsGoolTokkiRegular,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    val homeTab : TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val bodyMB: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val bodyLM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    val bodyXMM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val bodyMM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    val bodyMSB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val bodyXMR: TextStyle = TextStyle(
        fontFamily = HsGoolTokkiRegular,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val bodyMR: TextStyle = TextStyle(
        fontFamily = PretendardRegular,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    val bodyML: TextStyle = TextStyle(
        fontFamily = PretendardLight,
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    val bodyXSM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 11.sp,
        lineHeight = 15.sp
    ),
    val bodySM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val bodySSB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val bodySR: TextStyle = TextStyle(
        fontFamily = PretendardRegular,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val bodyLSB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
)