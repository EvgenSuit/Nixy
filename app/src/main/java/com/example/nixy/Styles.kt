package com.example.nixy

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val rubikDoodleFontFamily = FontFamily(
    Font(R.font.rubik_doodle_shadow_regular, FontWeight.Bold)
)
val rubikBubblesFontFamily = FontFamily(
    Font(R.font.rubik_bubbles_regular)
)

val typography = Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = rubikDoodleFontFamily,
        fontSize = 35.sp
    ),
    titleMedium = TextStyle(
        fontFamily = rubikBubblesFontFamily,
        fontSize = 35.sp
    ),
    titleSmall = TextStyle(
        fontSize = 20.sp
    )
)