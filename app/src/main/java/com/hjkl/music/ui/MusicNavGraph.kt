package com.hjkl.music.ui

import SongViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hjkl.music.ui.album.AlbumRoute
import com.hjkl.music.ui.album.AlbumViewModel
import com.hjkl.music.ui.song.SongRoute


@Composable
fun MusicNavGraph(
    navController: NavHostController = rememberNavController(),
    operateDrawerState: (Boolean) -> Boolean,
    startDestination: String = MusicDestinations.SONG_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = MusicDestinations.SONG_ROUTE
        ) { navBackStackEntry ->
            val songViewModel: SongViewModel = viewModel(
                factory = SongViewModel.provideFactory()
            )
            SongRoute(
                songViewModel = songViewModel,
                operateDrawerState = operateDrawerState,
            )
        }
        composable(route = MusicDestinations.ALBUM_ROUTE) {
            val albumViewModel: AlbumViewModel = viewModel(
                factory = AlbumViewModel.provideFactory()
            )
            AlbumRoute(
                albumViewModel = albumViewModel,
                operateDrawerState = operateDrawerState

            )
        }
    }
}
