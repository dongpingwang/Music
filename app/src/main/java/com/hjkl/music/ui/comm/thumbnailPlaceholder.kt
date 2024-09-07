package com.hjkl.music.ui.comm

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.hjkl.music.ui.theme.surfaceVariantDark
import com.hjkl.music.ui.theme.surfaceVariantLight

@Composable
internal fun thumbnailPlaceholderDefaultBrush(
    color: Color = thumbnailPlaceHolderDefaultColor()
): Brush {
    return SolidColor(color)
}

@Composable
private fun thumbnailPlaceHolderDefaultColor(
    isInDarkMode: Boolean = isSystemInDarkTheme()
): Color {
    return if (isInDarkMode) {
        surfaceVariantDark
    } else {
        surfaceVariantLight
    }
}
