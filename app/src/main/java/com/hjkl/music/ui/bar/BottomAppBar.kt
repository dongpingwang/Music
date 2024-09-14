package com.hjkl.music.ui.bar

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.comm.onTrue
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.dialog.PlaylistDialog
import com.hjkl.music.ui.custom.PlayerProgressButton
import com.hjkl.music.ui.theme.MusicTheme

@Composable
fun BottomMiniPlayer(
    uiState: PlayerUiState,
    onClick: () -> Unit,
    onTogglePlay: () -> Unit,
    onScrollToNext: () -> Unit = {},
    onScrollToPrevious: () -> Unit = {}
) {
    val curSong = uiState.curSong
    val isPlaying = uiState.isPlaying
    val hasPlayingContent: Boolean = curSong != null
    var showBottomSheet by remember { mutableStateOf(false) }
    val progress = when {
        hasPlayingContent -> (uiState.progressInMs.toFloat() / curSong!!.duration).coerceIn(
            0F,
            100F
        )

        else -> 0F
    }
    BottomAppBar(Modifier
        .clickable {
            if (hasPlayingContent) {
                onClick()
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumImage(
                data = curSong?.bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium),
                placeHolderImage = null
            )
            val desc = when {
                hasPlayingContent -> uiState.curSong?.title + " - " + uiState.curSong?.artist
                else -> uiState.randomNoPlayContentDesc
            }
            Text(
                text = desc,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp)
            )

            PlayerProgressButton(isPlaying = isPlaying, progress = progress) {
                hasPlayingContent.onTrue(onTogglePlay)
            }

            IconButton(
                onClick = {
                    if (uiState.playlist.isNotEmpty()) {
                        showBottomSheet = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.QueueMusic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    if (showBottomSheet) {
        PlaylistDialog(uiState = uiState, onDialogHide = { showBottomSheet = false })
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomMiniPlayerPreview() {
    MusicTheme {
        Surface {
            BottomMiniPlayer(
                uiState = FakeDatas.playerUiState,
                onClick = {},
                onTogglePlay = {}
            )
        }
    }
}