package com.hjkl.music.ui.comm

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ImageBackgroundColorScrim(
    data: Any?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        data = data,
        modifier = modifier,
        overlay = {
            drawRect(color)
        }
    )
}

@Composable
fun ImageBackgroundRadialGradientScrim(
    data: Any?,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        data = data,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset(0f, size.height),
                radius = size.width * 1.5f
            )
            drawRect(brush, blendMode = BlendMode.Multiply)
        }
    )
}

@Composable
fun ImageBackground(
    data: Any?,
    overlay: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = data,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
    )
}
