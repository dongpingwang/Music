package com.hjkl.music.ui

import SongUiState
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import asSuccess
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.theme.MusicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.song_title)) },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.menu_24px),
                    contentDescription = stringResource(R.string.menu)
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.search_24px),
                    contentDescription = stringResource(R.string.search)
                )
            }
        },
        modifier = modifier
    )
}


//@Composable
//fun ToLoading() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .wrapContentSize()
//    ) {
//        CircularProgressIndicator(
//            modifier = Modifier
//                .align(Alignment.Center)
//                .size(80.dp),
//            color = MaterialTheme.colorScheme.secondary,
//            trackColor = MaterialTheme.colorScheme.surfaceVariant
//        )
//        Text(
//            text = stringResource(id = R.string.loading_desc),
//            modifier = Modifier.align(Alignment.Center)
//        )
//    }
//}

@Composable
fun ToError() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = "加载失败")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomMiniPlayer(
    uiState: SongUiState,
    onClick: () -> Unit,
    onTogglePlay: () -> Unit
) {
    val curSong = uiState.asSuccess().curSong
    val isPlaying = uiState.asSuccess().isPlaying
    val hasPlayingContent: Boolean = curSong != null
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box {
        BottomAppBar(modifier = Modifier.clickable {
            if (hasPlayingContent) {
                onClick()
            }
        }) {

            if (curSong?.bitmap != null) {
                Image(
                    bitmap = curSong.bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.extraSmall)

                )
            } else {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.music_note_40px),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(Color.LightGray)
                )
            }
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
                    .padding(horizontal = 10.dp, vertical = 16.dp)
            )
            IconButton(
                onClick = {
                    if (hasPlayingContent) {
                        onTogglePlay()
                    }
                }

            ) {
                val playIcon = if (isPlaying) {
                    ImageVector.vectorResource(id = R.drawable.pause_circle_24px)
                } else {
                    ImageVector.vectorResource(id = R.drawable.play_circle_24px)
                }
                Icon(
                    imageVector = playIcon,
                    contentDescription = null,
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
                    imageVector = ImageVector.vectorResource(id = R.drawable.queue_music_24px),
                    contentDescription = null,
                )
            }
        }
        if (showBottomSheet && hasPlayingContent) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
            ) {
                Text(
                    text = "Page: $",
                    modifier = Modifier.height(600.dp)
                )
            }
        }
    }


}


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SongItemPreview() {
    MusicTheme {
        Surface {
            TopAppBar(openDrawer = { })
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomMiniPlayerPreview() {
    MusicTheme {
        Surface {
            BottomMiniPlayer(
                uiState = FakeDatas.songUiState,
                onClick = {},
                onTogglePlay = {}
            )
        }
    }
}