package com.example.taskcronometer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.taskcronometer.R

val NotoSans = FontFamily(
    Font(R.font.notosans_regular)
)

val Ubuntu = FontFamily(
    Font(R.font.ubunto_regular),
    Font(R.font.ubuntu_bold, FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    displayLarge = TextStyle(
        fontFamily = NotoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
)