package com.hjkl.music.ui

import android.net.Uri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder

object Destinations {
    const val HOME_ROUTE = "home"
    const val DETAIL_ALBUM_ROUTE = "album_detail"
    const val DETAIL_ARTIST_ROUTE = "artist_detail"
    const val DETAIL_FOLDER_ROUTE = "folder_detail"
    const val PLAYER_ROUTE = "player"
}

class NavigationActions(val navController: NavHostController) {

    val navigateToHome: () -> Unit = {
        navController.navigate(Destinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
        }
    }

    val navigateToPlayer: () -> Unit = {
        navController.navigate(Destinations.PLAYER_ROUTE)
    }

    val navigateToAlbumDetail: (Album) -> Unit = {
        navController.navigate("${Destinations.DETAIL_ALBUM_ROUTE}/${it.id}")
    }

    val navigateToArtistDetail: (Artist) -> Unit = {
        navController.navigate("${Destinations.DETAIL_ARTIST_ROUTE}/${it.id}")
    }

    val navigateToFolderDetail: (Folder) -> Unit = {
        navController.navigate("${Destinations.DETAIL_FOLDER_ROUTE}/${Uri.encode(it.path)}")
    }

    val popBackStack: () -> Unit = {
        navController.popBackStack()
    }
}