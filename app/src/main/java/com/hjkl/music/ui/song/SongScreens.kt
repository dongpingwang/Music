package com.hjkl.music.ui.song

import SongViewModel
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.BottomBarActions
import com.hjkl.music.ui.comm.PlayerActions
import com.hjkl.music.ui.comm.ScreenWithTopBottomBar
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
    val handler = ActionHandler.get()
    SongScreen(
        uiState = uiState,
        // 点击Toolbar菜单按钮，切换侧边栏
        topBarActions = TopBarActions(onDrawerClicked = onDrawerClicked),
        bottomBarActions = handler.bottomBarActions,
        playerActions = handler.playerActions,
        itemActions = handler.itemActions,
        // 点击扫描音乐按钮，进行扫描
        onScanMusic = { songViewModel.scanMusic() },
        // 下拉刷新
        onRefresh = { songViewModel.refresh() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    uiState: SongUiState,
    topBarActions: TopBarActions,
    bottomBarActions: BottomBarActions,
    playerActions: PlayerActions,
    itemActions: ItemActions,
    onScanMusic: () -> Unit,
    onRefresh: () -> Unit,
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
            (uiState.errorMsg != null) || (!uiState.isLoading && uiState.datas.isEmpty()) -> {
                ErrorOrEmpty(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    onScanMusic = onScanMusic
                )
                Spacer(modifier = Modifier.weight(1F))
            }

            else -> {
                SongList(
                    uiState = uiState.playerUiState,
                    isLoading = uiState.isLoading,
                    songs = uiState.datas,
                    onPlayAll = { itemActions.onPlayAll(uiState.datas) },
                    onItemClicked = {
                        scope.launch {
                            if (itemActions.onItemClicked(uiState.datas, it)) {
                                playerActions.onPlayerPageExpandChanged(true)
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    },
                    onPlayClicked = { itemActions.onPlayClicked(uiState.datas, it) },
                    onAddToQueue = itemActions.onAddToQueue,
                    onRefresh = onRefresh
                )
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun ErrorOrEmpty(modifier: Modifier = Modifier, onScanMusic: () -> Unit = {}) {
    val permission = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> android.Manifest.permission.READ_MEDIA_AUDIO
        else -> android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val permissionState = rememberPermissionState(permission = permission) {
        "onPermissionResult: $permission --> $it".d()
    }
    Button(onClick = {
        when {
            permissionState.status.isGranted -> {
                "$permission is isGranted".d()
                onScanMusic()
            }

            else -> {
                "launchPermissionRequest".d()
                permissionState.launchPermissionRequest()
            }
        }

    }, modifier = modifier) {
        Text(
            text = stringResource(id = R.string.scan_music),
            style = MaterialTheme.typography.titleMedium
        )
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
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = {
            onRefresh()
        },
        modifier = Modifier.weight(1F)
    ) {
        Column {
            HeaderSongItem(count = songs.size, onPlayAll = onPlayAll, onEdit = {})
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {

                itemsIndexed(songs) { index, song ->
                    val curSongPlaying = uiState.isPlaying && uiState.curSong?.id == song.id
                    SongItem(
                        isSongPlaying = curSongPlaying,
                        song = song,
                        onItemClicked = { onItemClicked(index) },
                        onPlayClicked = { onPlayClicked(index) },
                        onAddToQueue = { onAddToQueue(song) })
                }
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
                bottomBarActions = BottomBarActions(
                    onPlayToggle = {},
                    onScrollToNext = {},
                    onScrollToPrevious = {}),
                playerActions = PlayerActions(
                    onPlayerPageExpandChanged = {},
                    onPlayToggle = {},
                    onSeekBarValueChange = { isUserSeeking, progressInMillis -> },
                    onRepeatModeSwitch = {},
                    onShuffleModeEnable = {},
                    onPlayPrev = {},
                    onPlayNext = {}
                ),
                itemActions = ItemActions(
                    onPlayAll = {},
                    onItemClicked = { i, f -> false },
                    onPlayClicked = { i, f -> },
                    onAddToQueue = {}),
                onScanMusic = {},
                onRefresh = {}
            )
        }
    }
}
