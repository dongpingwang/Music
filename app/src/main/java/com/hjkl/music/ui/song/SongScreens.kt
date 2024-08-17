package com.hjkl.music.ui.song

import SongUiState
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Song
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.BottomMiniPlayer
import com.hjkl.music.ui.ToError
import com.hjkl.music.ui.ToLoading
import com.hjkl.music.ui.TopAppBar
import com.hjkl.music.ui.player.PlayerPage
import com.hjkl.music.ui.theme.MusicTheme
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
    onTogglePlay: () -> Unit
) {

    Log.d(TAG, "HomeScreenWithList(): $uiState")
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(openDrawer = openDrawer) },
        sheetContent = { PlayerPage(uiState = uiState) },
        sheetDragHandle = null,
        sheetPeekHeight = 0.dp,
        sheetShape = BottomSheetDefaults.HiddenShape,
    ) { innerPadding ->
        when (uiState) {
            is SongUiState.Loading -> {
                ToLoading()
            }

            is SongUiState.Error -> {
                ToError()
            }

            is SongUiState.Success -> {
                val isEmpty = uiState.songs.isEmpty()
                Column {
                    if (isEmpty) {

                    } else {
                        SongList(
                            songs = uiState.songs,
                            onPlayAll = onPlayAll,
                            onItemClick = onItemClick,
                            onRefresh = onRefresh
                        )
                    }
                    BottomMiniPlayer(
                        uiState = uiState,
                        onClick = {
                            scope.launch {
                                Log.d("wdp0", "bottomSheetState.expand")
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        onTogglePlay = onTogglePlay
                    )
                }
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.SongList(
    songs: List<Song>,
    onPlayAll: () -> Unit,
    onItemClick: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = { onRefresh() },
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
                onTogglePlay = {})
        }
    }
}
