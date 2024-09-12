package com.hjkl.music.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun PlayerProgressButton(isPlaying: Boolean, progress: Float, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onClick

        ) {
            val playIcon = when {
                isPlaying -> Icons.Filled.Pause
                else -> Icons.Filled.PlayArrow
            }
            Icon(
                imageVector = playIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        CircularProgressIndicator(progress = { progress }, trackColor = Color.Transparent)
    }
}