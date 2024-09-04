package com.hjkl.music.ui.song

import SongViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import asSuccess

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
        onPlayerPageExpandChanged = { songViewModel.player().setCurPage(if (it) 1 else 0) },
        onPlayAll = { songViewModel.player().playAll(uiState.asSuccess().songs) },
        onItemClick = { songViewModel.player().playIndex(uiState.asSuccess().songs, it) },
        onPlayToggle = { songViewModel.player().togglePlay() },
        onSeekBarValueChange = { isUserSeeking, progressRatio ->
            songViewModel.player().userInputSeekBar(isUserSeeking, progressRatio)
        },
        onPlaySwitchMode = { songViewModel.player().switchMode(it) },
        onPlayPrev = { songViewModel.player().playPrev() },
        onPlayNext = { songViewModel.player().playNext() }
    )
}



