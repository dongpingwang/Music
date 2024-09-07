package com.hjkl.music.ui.album

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.music.R
import com.hjkl.music.ui.comm.AlbumUiState
import com.hjkl.music.ui.comm.BottomBarActions
import com.hjkl.music.ui.comm.PlayerActions
import com.hjkl.music.ui.comm.ScreenWithTopBottomBar
import com.hjkl.music.ui.comm.TopBarActions

@Composable
fun AlbumScreen(
    onDrawerClicked: () -> Unit
) {
    val albumViewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModel.provideFactory()
    )
    val uiState by albumViewModel.uiState.collectAsStateWithLifecycle()
    AlbumScreen(
        uiState = uiState,
        topBarActions = TopBarActions(onDrawerClicked = onDrawerClicked),
        bottomBarActions = BottomBarActions(onPlayToggle = {}),
        playerActions = PlayerActions(
            onPlayerPageExpandChanged = {},
            onPlayToggle = {},
            onSeekBarValueChange = { i, f -> },
            onPlaySwitchMode = {},
            onPlayPrev = {},
            onPlayNext = {}
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    uiState: AlbumUiState,
    topBarActions: TopBarActions,
    bottomBarActions: BottomBarActions,
    playerActions: PlayerActions
) {
    ScreenWithTopBottomBar(
        uiState = uiState,
        title = stringResource(id = R.string.album_title),
        topBarActions = topBarActions,
        bottomBarActions = bottomBarActions,
        playerActions = playerActions
    ) {

    }
}