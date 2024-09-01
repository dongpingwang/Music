package com.hjkl.music.ui.song

import SongUiState
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import asSuccess
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.BottomMiniPlayer
import com.hjkl.music.ui.ToError
import com.hjkl.music.ui.TopAppBar
import com.hjkl.music.ui.player.PlayerPage
import com.hjkl.music.ui.theme.MusicTheme
import com.hjkl.player.constant.PlayMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    uiState: SongUiState,
    onRefresh: () -> Unit,
    operateDrawerState: (Boolean) -> Boolean,
    onPlayerPageExpandChanged:(Boolean) ->Unit,
    onPlayAll: () -> Unit,
    onItemClick: (Int) -> Unit,
    onPlayToggle: () -> Unit,
    onSeekBarValueChange: (Boolean, Float) -> Unit,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayNext: () -> Unit
) {
    "SongScreen() call: $uiState".d()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState= rememberStandardBottomSheetState(skipHiddenState = false))
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(openDrawer = { operateDrawerState(true) }) },
        sheetContent = {
            PlayerPage(
                uiState = uiState,
                onBackHandle = {
                    if (it == null) {
                        "手动点击返回按钮".d()
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                            onPlayerPageExpandChanged(false)
                        }
                        return@PlayerPage false
                    }
                    "收到按键事件".d()
                    if (it.key == Key.Back) {
                        "收到返回按键事件".d()
                        if (operateDrawerState(false)) {
                            "隐藏NavigationDrawer，拦截返回按键事件".d()
                            return@PlayerPage true
                        }
                        val expanded = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                        if (expanded) {
                            scope.launch {
                                scaffoldState.bottomSheetState.hide()
                                onPlayerPageExpandChanged(false)
                            }
                            "隐藏BottomSheet，拦截返回按键事件".d()
                            return@PlayerPage true
                        }
                    }
                    "不拦截按键事件".d()
                    false
                },
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
        sheetShape = BottomSheetDefaults.HiddenShape
    ) { innerPadding ->
        when (uiState) {

            is SongUiState.Error -> {
                ToError()
            }

            is SongUiState.Success -> {
                Column {
                    SongList(
                        uiState = uiState,
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
                                onPlayerPageExpandChanged(true)
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
    uiState: SongUiState,
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
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            state = listState
        ) {
            item {
                HeaderSongItem(count = songs.size, onPlayAll = onPlayAll, onMoreClick = {})
            }
            itemsIndexed(songs) { index, song ->
                val curSongPlaying = uiState.asSuccess().isPlaying && uiState.asSuccess().curSong?.id == song.id
                SongItem(isSongPlaying = curSongPlaying, song = song, onItemClick = { onItemClick(index) }, onMoreClick = {})
            }
        }

        FloatingActionButton(
            onClick = {
                "FloatingActionButton click".d()
                val curSongPosition =
                    uiState.asSuccess().songs.indexOfLast { it.id == uiState.asSuccess().curSong?.id }
                scope.launch { listState.scrollToItem(curSongPosition) }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.my_location_24px),
                contentDescription = null
            )
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
                operateDrawerState = { false },
                onPlayerPageExpandChanged = {},
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
