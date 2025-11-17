package com.example.melora.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.melora.R

// Set of Material typography styles to start with

val Lato = FontFamily(
        Font(R.font.lato_regular, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.lato_bold, FontWeight.Bold, FontStyle.Normal),
        Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.lato_boldtalic, FontWeight.Bold, FontStyle.Italic)
)

val PlayfairDisplay = FontFamily(
    Font(R.font.playfairdisplay_variablefont_weight, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.playfairdisplay_italic_variablefont_weight, FontWeight.Normal, FontStyle.Italic),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)