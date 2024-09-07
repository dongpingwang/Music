package com.hjkl.music.ui.comm

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.music.R
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.theme.MusicTheme

@Composable
fun BottomMiniPlayer(
    uiState: PlayerUiState,
    onClick: () -> Unit,
    onTogglePlay: () -> Unit
) {
    val curSong = uiState.curSong
    val isPlaying = uiState.isPlaying
    val hasPlayingContent: Boolean = curSong != null
    var showBottomSheet by remember { mutableStateOf(false) }

    BottomAppBar(modifier = Modifier
        .clickable {
            if (hasPlayingContent) {
                onClick()
            }
        }) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumImage(
                data = curSong?.bitmap, contentDescription = null, modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            val desc = if (curSong != null) {
                "${curSong.title} - ${curSong.artist}"
            } else {
                stringResource(id = R.string.no_play_desc)
            }
            Text(
                text = desc,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = {
                    if (hasPlayingContent) {
                        onTogglePlay()
                    }
                }

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

            IconButton(
                onClick = {
                    if (hasPlayingContent) {
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
        PlaylistDialog(onDialogHide = { showBottomSheet = false })
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomMiniPlayerPreview() {
    MusicTheme {
        Surface {
            BottomMiniPlayer(
                uiState = FakeDatas.songUiState.playerUiState,
                onClick = {},
                onTogglePlay = {}
            )
        }
    }
}