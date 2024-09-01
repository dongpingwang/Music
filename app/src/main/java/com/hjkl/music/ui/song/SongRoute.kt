package com.hjkl.music.ui.song

import SongViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SongRoute(
    songViewModel: SongViewModel,
    operateDrawerState: (Boolean) -> Boolean,
) {
    val uiState by songViewModel.uiState.collectAsStateWithLifecycle()

    SongScreen(
        uiState = uiState,
        onRefresh = { songViewModel.refresh() },
        operateDrawerState = operateDrawerState,
        onPlayerPageExpandChanged = {songViewModel.setCurPage(if (it) 1 else 0)},
        onPlayAll = { songViewModel.playAll() },
        onItemClick = { songViewModel.playIndex(it) },
        onPlayToggle = { songViewModel.togglePlay() },
        onSeekBarValueChange = { isUserSeeking, progressRatio ->
            songViewModel.userInputSeekBar(isUserSeeking, progressRatio)
        },
        onPlaySwitchMode = { songViewModel.switchMode(it) },
        onPlayPrev = { songViewModel.playPrev() },
        onPlayNext = { songViewModel.playNext() }
    )
}



