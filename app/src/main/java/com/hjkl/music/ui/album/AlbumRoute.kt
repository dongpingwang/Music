package com.hjkl.music.ui.album


import androidx.compose.runtime.Composable


@Composable
fun AlbumRoute(
    albumViewModel: AlbumViewModel,
    operateDrawerState: (Boolean) -> Boolean
) {
    AlbumScreen(openDrawer = { operateDrawerState(true) })
}