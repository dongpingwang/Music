package com.hjkl.music.ui.album

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.BottomMiniPlayer
import com.hjkl.music.ui.TopAppBar

@Composable
fun AlbumScreen(
    openDrawer: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = stringResource(id = R.string.album_title),
                openDrawer = { openDrawer() })
        }, bottomBar = {
            BottomMiniPlayer(
                uiState = FakeDatas.songUiState,
                onClick = {

                },
                onTogglePlay = { }
            )
        }) { innerPadding ->


    }
}