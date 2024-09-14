package com.hjkl.music.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hjkl.comm.d
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.ui.album.AlbumDetailScreens
import com.hjkl.music.ui.album.AlbumScreen
import com.hjkl.music.ui.album.AlbumViewModel
import com.hjkl.music.ui.artist.ArtistScreen
import com.hjkl.music.ui.folder.FolderScreen
import com.hjkl.music.ui.home.Screen
import com.hjkl.music.ui.player.PlayerPages
import com.hjkl.music.ui.song.SongScreen

@Composable
fun MusicNavGraph(
    drawScreen: Screen,
    navigationActions: NavigationActions,
    playerUiState: PlayerUiState,
    onDrawerClicked: () -> Unit = {},
    startDestination: String = Destinations.HOME_ROUTE,
) {
    NavHost(
        navController = navigationActions.navController,
        startDestination = startDestination
    ) {
        composable(
            route = Destinations.HOME_ROUTE
        ) {
            when (drawScreen) {
                Screen.Home -> {
                    SongScreen(
                        onDrawerClicked = onDrawerClicked,
                        onOpenPlayer = navigationActions.navigateToPlayer
                    )
                }

                Screen.ALBUM -> {
                    AlbumScreen(onDrawerClicked = onDrawerClicked, onCardClicked = {
                        navigationActions.navigateToAlbumDetail(it)
                    })
                }

                Screen.ARTIST -> {
                    ArtistScreen(onDrawerClicked = onDrawerClicked)
                }

                Screen.FOLDER -> {
                    FolderScreen(onDrawerClicked = onDrawerClicked)
                }

                Screen.FAVORITE -> {}
                Screen.MYLIST -> {}
                Screen.SETTING -> {}
            }
        }


        composable(
            route = "${Destinations.DETAIL_ALBUM_ROUTE}/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getInt("albumId")
            "albumId: $albumId".d()
            val albumViewModel: AlbumViewModel = viewModel(
                factory = AlbumViewModel.provideFactory()
            )
            val uiState by albumViewModel.uiState.collectAsStateWithLifecycle()
            uiState.datas.find { it.id == albumId }?.let {
                AlbumDetailScreens(it, onBackClicked = navigationActions.popBackStack)
            }
        }

        composable(Destinations.PLAYER_ROUTE) {
            PlayerPages(uiState = playerUiState, onBackPress = {
                navigationActions.popBackStack()
            })
        }
    }
}