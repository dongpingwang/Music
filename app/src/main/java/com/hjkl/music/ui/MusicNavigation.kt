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

/**
 * Models the navigation actions in the app.
 */
class JetnewsNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(MusicDestinations.SONG_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val navigateToInterests: () -> Unit = {
        navController.navigate(MusicDestinations.ARTIST_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
