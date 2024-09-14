package com.hjkl.music.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.hjkl.entity.Album

object Destinations {

    const val HOME_ROUTE = "home"

    const val DETAIL_ALBUM_ROUTE = "album_detail"

    const val PLAYER_ROUTE = "player"
}

class NavigationActions(val navController: NavHostController) {

    val navigateToHome: () -> Unit = {
        navController.navigate(Destinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }


    val navigateToPlayer: () -> Unit = {
        navController.navigate(Destinations.PLAYER_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAlbumDetail: (Album) -> Unit = {
        navController.navigate("${Destinations.DETAIL_ALBUM_ROUTE}/${it.id}") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    val popBackStack: () -> Unit = {
        navController.popBackStack()
    }
}