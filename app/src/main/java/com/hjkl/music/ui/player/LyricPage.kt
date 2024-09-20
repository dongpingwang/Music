package com.hjkl.music.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.comm.d
import com.hjkl.music.R
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.model.Lyric
import com.hjkl.music.parser.LyricParser
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.utils.DisplayUtil
import kotlinx.coroutines.launch

@Composable
fun LyricPage(
    uiState: PlayerUiState,
    lyricState: Lyric?,
    onSeekTo: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        SongInfo(uiState)
        if (lyricState == null || lyricState.lines.isEmpty()) {
            LyricNotFound()
        } else {
            LyricList(uiState = uiState, lyricState = lyricState, onSeekTo = onSeekTo)
        }
    }
}

@Composable
private fun SongInfo(uiState: PlayerUiState) {
    Text(
        text = DisplayUtil.getDisplayTitle(uiState.curSong?.title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    )
    Text(
        text = DisplayUtil.getDisplayArtist(uiState.curSong?.artist),
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    )
}

@Composable
private fun LyricNotFound() {
    Box(
        modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.lyric_not_found))
    }
}

@Composable
private fun LyricList(
    uiState: PlayerUiState,
    lyricState: Lyric,
    onSeekTo: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var isUserTouching by remember { mutableStateOf(false) }
    val lineHeight = with(LocalDensity.current) { (56).dp.toPx() }.toInt()
    val curLineIndex = LyricParser.getLyricLineIndex(uiState.progressInMs, lyricState)
    var spacerCount by remember { mutableStateOf(0) }
    LaunchedEffect(curLineIndex) {
        scope.launch {
            if (curLineIndex >= 0 && !isUserTouching) {
                listState.animateScrollToItem(
                    curLineIndex, -2 * lineHeight
                )
                spacerCount = (10 - (lyricState.lines.size - curLineIndex)).coerceAtLeast(0).coerceAtMost(10)
            }
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { isScrollInProgress ->
            isUserTouching = isScrollInProgress
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(560.dp),
        state = listState,
    ) {
        itemsIndexed(lyricState.lines) { index, content ->
            Text(
                text = content.text ?: "",
                style = when {
                    curLineIndex == index -> MaterialTheme.typography.titleLarge
                    else -> MaterialTheme.typography.titleMedium
                },
                color = when {
                    curLineIndex == index -> MaterialTheme.colorScheme.primary
                    else -> Color.Unspecified
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSeekTo(content.startTimeInMillis)
                    }
                    .padding(vertical = 16.dp)
            )
        }
        items(spacerCount) {
            Spacer(modifier = Modifier.height(56.dp))
        }
    }

}

@Preview
@Composable
private fun LyricPagePreview() {
    LyricPage(uiState = FakeDatas.playerUiState, lyricState = FakeDatas.lyric, onSeekTo = {})
}