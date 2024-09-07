package com.hjkl.music.ui.song

import SongViewModel
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.BottomBarActions
import com.hjkl.music.ui.comm.PlayerActions
import com.hjkl.music.ui.comm.ScreenWithTopBottomBar
import com.hjkl.music.ui.comm.ToError
import com.hjkl.music.ui.comm.SongUiState
import com.hjkl.music.ui.comm.TopBarActions
import com.hjkl.music.ui.theme.MusicTheme
import kotlinx.coroutines.launch

@Composable
fun SongScreen(
    onDrawerClicked: () -> Unit,
) {
    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModel.provideFactory()
    )
    val uiState by songViewModel.uiState.collectAsStateWithLifecycle()
    SongScreen(
        uiState = uiState,
        // 点击Toolbar菜单按钮，切换侧边栏
        topBarActions = TopBarActions(onDrawerClicked = onDrawerClicked),
        // 点击BottomBar中播放按钮，切换播放状态
        bottomBarActions = BottomBarActions(onPlayToggle = { songViewModel.player().togglePlay() }),
        playerActions = PlayerActions(
            // 切换到播放界面事件分发
            onPlayerPageExpandChanged = { songViewModel.player().setCurPage(if (it) 1 else 0) },
            // 切换播放状态
            onPlayToggle = { songViewModel.player().togglePlay() },
            // 调节进度
            onSeekBarValueChange = { isUserSeeking, progressInMillis ->
                songViewModel.player().userInputSeekBar(isUserSeeking, progressInMillis)
            },
            // 切换播放模式
            onPlaySwitchMode = {
                songViewModel.player().switchMode(it)
            },
            // 上一曲
            onPlayPrev = { songViewModel.player().playPrev() },
            // 下一曲
            onPlayNext = { songViewModel.player().playNext() }
        ),
        // 下拉刷新
        onRefresh = { songViewModel.refresh() },
        // 播放全部
        onPlayAll = { songViewModel.player().playAll(uiState.datas) },
        // 点击列表条目，进到播放界面
        onItemClicked = { songViewModel.player().maybePlayIndex(uiState.datas, it) },
        // 点击列表条目中的播放按钮
        onPlayClicked = { songViewModel.player().maybeTogglePlay(uiState.datas, it) },
        // 点击列表条目中的添加到下一曲按钮
        onAddToQueue = { songViewModel.player().addToNextPlay(it) }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    uiState: SongUiState,
    topBarActions: TopBarActions,
    bottomBarActions: BottomBarActions,
    playerActions: PlayerActions,
    onRefresh: () -> Unit,
    onPlayAll: () -> Unit,
    onItemClicked: (Int) -> Unit,
    onPlayClicked: (Int) -> Unit,
    onAddToQueue: (Song) -> Unit
) {
    val scope = rememberCoroutineScope()
    ScreenWithTopBottomBar(
        uiState = uiState,
        title = stringResource(id = R.string.song_title),
        topBarActions = topBarActions,
        bottomBarActions = bottomBarActions,
        playerActions = playerActions
    ) { scaffoldState ->
        when {
            uiState.errorMsg != null -> {
                ToError()
            }

            else -> {
                SongList(
                    uiState = uiState.playerUiState,
                    isLoading = uiState.isLoading,
                    songs = uiState.datas,
                    onPlayAll = onPlayAll,
                    onItemClicked = {
                        scope.launch {
                            onItemClicked(it)
                            playerActions.onPlayerPageExpandChanged(true)
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    onPlayClicked = onPlayClicked,
                    onAddToQueue = onAddToQueue,
                    onRefresh = onRefresh
                )
                val isEmpty = uiState.datas.isEmpty()
                if (!uiState.isLoading && isEmpty) {
                    EmptyTips()
                }
            }
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
    uiState: PlayerUiState,
    isLoading: Boolean,
    songs: List<Song>,
    onPlayAll: () -> Unit,
    onItemClicked: (Int) -> Unit,
    onPlayClicked: (Int) -> Unit,
    onAddToQueue: (Song) -> Unit,
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
                HeaderSongItem(count = songs.size, onPlayAll = onPlayAll, onEdit = {})
            }
            itemsIndexed(songs) { index, song ->
                val curSongPlaying = uiState.isPlaying && uiState.curSong?.id == song.id
                SongItem(
                    isSongPlaying = curSongPlaying,
                    song = song,
                    onItemClicked = { onItemClicked(index) },
                    onPlayClicked = { onPlayClicked(index) },
                    onAddToQueue = { onAddToQueue(song) },
                    onMoreClick = {})
            }
        }

        FloatingActionButton(
            onClick = {
                if (uiState.curSong != null) {
                    val curSongPosition =
                        songs.indexOfLast { it.id == uiState.curSong.id }
                    scope.launch { listState.scrollToItem(curSongPosition) }
                } else {
                    scope.launch { listState.scrollToItem(0) }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
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
                topBarActions = TopBarActions(onDrawerClicked = {}),
                bottomBarActions = BottomBarActions(onPlayToggle = {}),
                playerActions = PlayerActions(
                    onPlayerPageExpandChanged = {},
                    onPlayToggle = {},
                    onSeekBarValueChange = { isUserSeeking, progressInMillis -> },
                    onPlaySwitchMode = {},
                    onPlayPrev = {},
                    onPlayNext = {}
                ),
                onRefresh = {},
                onPlayAll = {},
                onItemClicked = {},
                onPlayClicked = {},
                onAddToQueue = {}
            )
        }
    }
}
