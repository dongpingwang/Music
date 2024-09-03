package com.hjkl.music.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


object MusicDestinations {
    const val SONG_ROUTE = "song"
    const val ALBUM_ROUTE = "album"
    const val ARTIST_ROUTE = "artist"
    const val FOLDER_ROUTE = "folder"
    const val FAVORITE_ROUTE = "favorite"
    const val SONGLIST_ROUTE = "songlist"
    const val SETTING_ROUTE = "setting"
    const val ABOUT_ROUTE = "about"
}


class MusicNavigationActions(private val navController: NavHostController) {
    private fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToHome: () -> Unit = { navigate(MusicDestinations.SONG_ROUTE) }
    val navigateToAlbum: () -> Unit = { navigate(MusicDestinations.ALBUM_ROUTE) }
}
