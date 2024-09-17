package com.hjkl.music.ui.player

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.model.Lyric
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.dialog.PlayerPageMoreDialog
import com.hjkl.player.constant.RepeatMode

private val defaultPageIndex = 1
private val pageCount = 3


@Composable
fun PlayerPages(uiState: PlayerUiState) {
    val playerActions = ActionHandler.get().playerActions
    val navigationActions = ActionHandler.get().navigationActions
    val lyricViewModel: LyricViewModel = viewModel(
        factory = LyricViewModel.provideFactory()
    )
    val curLyricState by lyricViewModel.curLyricState.collectAsStateWithLifecycle()
    PlayerPages(
        uiState = uiState,
        lyricState = curLyricState,
        onBackPress = navigationActions.popBackStack,
        onValueChange = playerActions.onSeekBarValueChange,
        onRepeatModeSwitch = playerActions.onRepeatModeSwitch,
        onShuffleModeEnable = playerActions.onShuffleModeEnable,
        onPlayPrev = playerActions.onPlayPrev,
        onPlayToggle = playerActions.onPlayToggle,
        onPlayNext = playerActions.onPlayNext,
        onArtistClicked = navigationActions.navigateToArtistDetail,
        onAlbumClicked = navigationActions.navigateToAlbumDetail
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayerPages(
    uiState: PlayerUiState,
    lyricState: Lyric?,
    onBackPress: () -> Unit,
    onValueChange: (Boolean, Long) -> Unit,
    onRepeatModeSwitch: (RepeatMode) -> Unit,
    onShuffleModeEnable: (Boolean) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit,
    onArtistClicked: (Artist) -> Unit,
    onAlbumClicked: (Album) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = defaultPageIndex) { pageCount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
    ) {
        TopAppBar(pagerState = pagerState, onBackPress = onBackPress)
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {
                    // 歌曲详情等界面
                    ExtSongInfoPage(
                        uiState = uiState,
                        lyric = lyricState,
                        onArtistClicked = onArtistClicked,
                        onAlbumClicked = onAlbumClicked
                    )
                }

                1 -> {
                    // 播放器界面
                    PlayerContentRegular(
                        uiState = uiState,
                        onValueChange = onValueChange,
                        onRepeatModeSwitch = onRepeatModeSwitch,
                        onShuffleModeEnable = onShuffleModeEnable,
                        onPlayPrev = onPlayPrev,
                        onPlayToggle = onPlayToggle,
                        onPlayNext = onPlayNext
                    )
                }

                2 -> {
                    // 歌词界面
                    LyricPage(
                        uiState = uiState,
                        lyricState = lyricState,
                        onSeekTo = { onValueChange(false, it) })
                }
            }
        }
    }
}


@Composable
private fun TopAppBar(
    pagerState: PagerState,
    onBackPress: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth()) {
        IconButton(onClick = { onBackPress() }) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .weight(1F),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                when (iteration) {
                    pagerState.currentPage -> {
                        Box(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .size(width = 16.dp, height = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .size(6.dp),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }
                }
            }
        }

        IconButton(onClick = { showBottomSheet = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
    if (showBottomSheet) {
        PlayerPageMoreDialog(onDialogHide = { showBottomSheet = false })
    }
}

@Preview
@Composable
private fun PlayerPagesPreview() {
    PlayerPages(
        FakeDatas.playerUiState,
        lyricState = null,
        onBackPress = { },
        onValueChange = { i, f -> },
        onRepeatModeSwitch = {},
        onShuffleModeEnable = {},
        onPlayPrev = {},
        onPlayToggle = {},
        onPlayNext = {},
        onArtistClicked = {},
        onAlbumClicked = {})
}

