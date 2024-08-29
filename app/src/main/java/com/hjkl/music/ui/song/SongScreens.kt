package com.hjkl.music.ui.song

import SongUiState
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Song
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.BottomMiniPlayer
import com.hjkl.music.ui.ToError
import com.hjkl.music.ui.TopAppBar
import com.hjkl.music.ui.player.PlayerPage
import com.hjkl.music.ui.theme.MusicTheme
import com.hjkl.player.constant.PlayMode
import kotlinx.coroutines.launch

private const val TAG = "SongScreens"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    uiState: SongUiState,
    onRefresh: () -> Unit,
    openDrawer: () -> Unit,
    onPlayAll: () -> Unit,
    onItemClick: (Int) -> Unit,
    onPlayToggle: () -> Unit,
    onSeekBarValueChange: (Boolean, Float) -> Unit,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayNext: () -> Unit
) {
    Log.d(TAG, "SongScreen() call: $uiState")

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(openDrawer = openDrawer) },
        sheetContent = {
            PlayerPage(
                uiState = uiState,
                onBackClick = {},
                onValueChange = onSeekBarValueChange,
                onPlaySwitchMode = onPlaySwitchMode,
                onPlayPrev = onPlayPrev,
                onPlayToggle = onPlayToggle,
                onPlayNext = onPlayNext
            )
        },
        sheetDragHandle = null,
        sheetPeekHeight = 0.dp,
        sheetMaxWidth = Dp.Infinity,
        sheetShape = BottomSheetDefaults.HiddenShape,
    ) { innerPadding ->
        when (uiState) {

            is SongUiState.Error -> {
                ToError()
            }

            is SongUiState.Success -> {
                Column {
                    SongList(
                        isLoading = uiState.isLoading,
                        songs = uiState.songs,
                        onPlayAll = onPlayAll,
                        onItemClick = onItemClick,
                        onRefresh = onRefresh
                    )
                    val isEmpty = uiState.songs.isEmpty()
                    if (!uiState.isLoading && isEmpty) {
                        EmptyTips()
                    }
                    BottomMiniPlayer(
                        uiState = uiState,
                        onClick = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        onTogglePlay = onPlayToggle
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun ColumnScope.EmptyTips() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(1F), contentAlignment = Alignment.Center
    ) {
        Text(text = "空空如也~")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.SongList(
    isLoading: Boolean,
    songs: List<Song>,
    onPlayAll: () -> Unit,
    onItemClick: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = {
            onRefresh()
        },
        modifier = Modifier.weight(1F)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            state = rememberLazyListState()
        ) {
            item {
                HeaderSongItem(count = songs.size, onPlayAll = onPlayAll, onMoreClick = {})
            }
            itemsIndexed(songs) { index, song ->
                SongItem(song = song, onItemClick = { onItemClick(index) }, onMoreClick = {})
            }
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeFeedScreenPreview() {
    MusicTheme {
        Surface {
            SongScreen(
                uiState = FakeDatas.songUiState,
                onRefresh = {},
                openDrawer = {},
                onPlayAll = {},
                onItemClick = {},
                onPlayToggle = {},
                onSeekBarValueChange = { isUserSeeking, progressRatio -> },
                onPlaySwitchMode = { playMode -> },
                onPlayPrev = {},
                onPlayNext = {})
        }
    }
}
