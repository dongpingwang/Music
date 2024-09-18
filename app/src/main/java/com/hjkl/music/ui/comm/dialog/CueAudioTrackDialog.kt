package com.hjkl.music.ui.comm.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.model.CueAudio
import com.hjkl.music.parser.CueAudioParser
import com.hjkl.music.test.FakeDatas


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CueAudioTrackDialog(
    playerUiState: PlayerUiState,
    cueAudio: CueAudio,
    onPlayTrack: (Long) -> Unit,
    onDialogHide: () -> Unit = {}
) {
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val screenHeightPx = displayMetrics.heightPixels
    val screenHeightDp = (screenHeightPx / displayMetrics.density).dp
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDialogHide()
        },
    ) {
        val curPlayTrackIndex =
            CueAudioParser.getPlayedTrackIndex(playerUiState.progressInMs, cueAudio)
        val highColor = MaterialTheme.colorScheme.primary
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = curPlayTrackIndex)
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = cueAudio.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = cueAudio.performer,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${cueAudio.tracks.size} Tracks",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .height(screenHeightDp * 0.6F)
                .padding(top = 8.dp),
            state = listState
        ) {
            itemsIndexed(cueAudio.tracks) { index, cueTrack ->
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onPlayTrack(cueTrack.startTime)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = cueTrack.title,
                        color = when {
                            index == curPlayTrackIndex -> highColor
                            else -> Color.Unspecified
                        },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = CueAudioParser.getTrackNumber(
                            index,
                            playerUiState.curSong?.duration ?: 0,
                            cueAudio
                        ),
                        color = when {
                            index == curPlayTrackIndex -> highColor
                            else -> Color.Unspecified
                        },
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CueAudioTrackDialogPreview() {
    CueAudioTrackDialog(
        playerUiState = FakeDatas.playerUiState,
        cueAudio = FakeDatas.cueAudio,
        onPlayTrack = {},
        onDialogHide = {}
    )
}