package com.hjkl.music.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hjkl.comm.d
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.ui.album.AlbumDetailScreens
import com.hjkl.music.ui.album.AlbumScreen
import com.hjkl.music.ui.artist.ArtistDetailScreens
import com.hjkl.music.ui.artist.ArtistScreen
import com.hjkl.music.ui.favorite.FavoriteScreens
import com.hjkl.music.ui.folder.FolderDetailScreen
import com.hjkl.music.ui.folder.FolderScreen
import com.hjkl.music.ui.home.Screen
import com.hjkl.music.ui.player.PlayerPages
import com.hjkl.music.ui.setting.ScanAudioSettingScreen
import com.hjkl.music.ui.setting.SettingScreens
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
                    SongScreen(onDrawerClicked = onDrawerClicked)
                }

                Screen.ALBUM -> {
                    AlbumScreen(onDrawerClicked = onDrawerClicked)
                }

                Screen.ARTIST -> {
                    ArtistScreen(onDrawerClicked = onDrawerClicked)
                }

                Screen.FOLDER -> {
                    FolderScreen(onDrawerClicked = onDrawerClicked)
                }

                Screen.SETTING -> {
                    SettingScreens(onDrawerClicked = onDrawerClicked)
                }

                Screen.FAVORITE -> {
                    FavoriteScreens(onDrawerClicked = onDrawerClicked)
                }
//                Screen.MYLIST -> {}

                else -> {
                    Text(
                        text = "$drawScreen is under construction.",
                        modifier = Modifier
                            .systemBarsPadding()
                            .padding(64.dp)
                    )
                }
            }
        }
        composable(
            route = Destinations.SETTING_SCAN_AUDIO
        ) {
            ScanAudioSettingScreen()
        }

        composable(
            route = "${Destinations.DETAIL_ALBUM_ROUTE}/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getInt("albumId")
            "albumId: $albumId".d()
            albumId?.let {
                AlbumDetailScreens(albumId = it)
            }
        }

        composable(
            route = "${Destinations.DETAIL_ARTIST_ROUTE}/{artistId}",
            arguments = listOf(navArgument("artistId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getInt("artistId")
            "artistId: $artistId".d()
            artistId?.let {
                ArtistDetailScreens(artistId = it)
            }
        }

        composable(
            route = "${Destinations.DETAIL_FOLDER_ROUTE}/{folderPath}",
            arguments = listOf(navArgument("folderPath") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val folderPath = Uri.decode(backStackEntry.arguments?.getString("folderPath"))
            "folderPath: $folderPath".d()
            folderPath?.let {
                FolderDetailScreen(folderPath = it)
            }
        }

        composable(Destinations.PLAYER_ROUTE) {
            PlayerPages(uiState = playerUiState)
        }
    }
}