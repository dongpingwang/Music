package com.hjkl.music.ui.song

import SongViewModel
import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.hjkl.comm.ResUtil
import com.hjkl.comm.ToastUtil
import com.hjkl.comm.d
import com.hjkl.comm.onTrue
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.SongUiState
import com.hjkl.music.ui.theme.MusicTheme
import kotlinx.coroutines.launch

@Composable
fun SongScreen(
    onDrawerClicked: () -> Unit
) {
    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModel.provideFactory()
    )
    val uiState by songViewModel.uiState.collectAsStateWithLifecycle()
    val playerUiState by songViewModel.playerUiState.collectAsStateWithLifecycle()
    val actionHandler = ActionHandler
    val itemActions = actionHandler.itemActions
    SongScreen(
        uiState = uiState,
        playerUiState = playerUiState,
        // 点击Toolbar菜单按钮，切换侧边栏
        onDrawerClicked = onDrawerClicked,
        onPlayAll = { itemActions.onPlayAll(uiState.datas) },
        onItemClicked = {
            itemActions.onItemClicked(uiState.datas, it).onTrue {
                actionHandler.navigationActions.navigateToPlayer()
            }
        },
        onPlayClicked = { itemActions.onPlayClicked(uiState.datas, it) },
        onAddToQueue = itemActions.onAddToQueue,
        // 点击扫描音乐按钮，进行扫描
        onScanMusic = { songViewModel.scanMusic() },
        // 下拉刷新
        onRefresh = { songViewModel.refresh() }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongScreen(
    uiState: SongUiState,
    playerUiState: PlayerUiState,
    onDrawerClicked: () -> Unit,
    onPlayAll: () -> Unit,
    onItemClicked: (Int) -> Unit,
    onPlayClicked: (Int) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onScanMusic: () -> Unit,
    onRefresh: () -> Unit,
) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = R.string.song_title)) },
            navigationIcon = {
                IconButton(onClick = onDrawerClicked) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }
        )
        when {
            (uiState.errorMsg != null) || (uiState.isFetchCompleted && uiState.datas.isEmpty()) -> {
                ErrorOrEmpty(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    onScanMusic = onScanMusic
                )
            }

            uiState.datas.isNotEmpty() -> {
                SongList(
                    uiState = playerUiState,
                    isLoading = !uiState.isFetchCompleted,
                    songs = uiState.datas,
                    onPlayAll = onPlayAll,
                    onItemClicked = onItemClicked,
                    onPlayClicked = onPlayClicked,
                    onAddToQueue = onAddToQueue,
                    onRefresh = onRefresh
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun ErrorOrEmpty(modifier: Modifier = Modifier, onScanMusic: () -> Unit = {}) {
    val permissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )

        else -> listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions) {
        "onPermissionsResult: ${it.keys} --> ${it.values}".d()
        if (it.values.firstOrNull { !it } == false) {
            ToastUtil.toast(ResUtil.getString(R.string.toast_permission_reject))
        }
    }

    Button(onClick = {
        when {
            permissionsState.allPermissionsGranted -> {
                "$permissions is isGranted".d()
                onScanMusic()
            }

            else -> {
                "launchPermissionRequest".d()
                permissionsState.launchMultiplePermissionRequest()
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
private fun SongList(
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
        }
    ) {
        Column {
            HeaderSongItem(count = songs.size,
                onPlayAll = onPlayAll,
                onEdit = {})
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {

                itemsIndexed(songs) { index, song ->
                    val curSongPlaying = uiState.isPlaying && uiState.curSong?.songId == song.songId
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
                        songs.indexOfLast { it.songId == uiState.curSong.songId }
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
                playerUiState = FakeDatas.playerUiState,
                onDrawerClicked = {},
                onPlayAll = {},
                onItemClicked = {},
                onPlayClicked = {},
                onAddToQueue = {},
                onScanMusic = {},
                onRefresh = {})
        }
    }
}
