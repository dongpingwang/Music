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
        onTogglePlay = { songViewModel.togglePlay() }
    )
}



