package com.hjkl.music.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hjkl.music.ui.theme.MusicTheme
import kotlinx.coroutines.launch

@Composable
fun MusicApp(
) {
    MusicTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            JetnewsNavigationActions(navController)
        }

        val coroutineScope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: MusicDestinations.SONG_ROUTE

        val drawerState = rememberDrawerState(DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = MusicDestinations.SONG_ROUTE,
                    navigateToSong = { },
                    navigateToAlbum = { },
                    navigateToArtist = {},
                    navigateToFolder = {},
                    navigateToFavorite = {},
                    navigateToSonglist = {},
                    navigateToSetting = {},
                    navigateToAbout = {},
                    closeDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }, drawerState = drawerState
        ) {
            Row {
                MusicNavGraph(
                    navController = navController,
                    operateDrawerState = { willState ->
                        // 参数：表示可能要设置的状态
                        // 返回值：表示是否设置了新状态
                        if (willState && drawerState.isClosed) {
                            coroutineScope.launch { drawerState.open() }
                            return@MusicNavGraph true
                        }
                        if (!willState && drawerState.isOpen) {
                            coroutineScope.launch { drawerState.close() }
                            return@MusicNavGraph true
                        }
                        false
                    }
                )
            }
        }
    }
}
