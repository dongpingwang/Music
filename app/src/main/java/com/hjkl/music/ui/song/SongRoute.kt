package com.hjkl.music.ui.song

import SongViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SongRoute(
    songViewModel: SongViewModel,
    openDrawer: () -> Unit,
) {
    val uiState by songViewModel.uiState.collectAsStateWithLifecycle()

    SongScreen(
        uiState = uiState,
        onRefresh = { songViewModel.refresh() },
        openDrawer = openDrawer,
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



