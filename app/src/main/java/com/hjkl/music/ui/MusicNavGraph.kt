

package com.hjkl.music.ui

import SongViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                factory = object : ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SongViewModel() as T
                    }
                }
            )
            SongRoute(
                songViewModel = songViewModel,
                operateDrawerState = operateDrawerState,
            )
        }
        composable(MusicDestinations.ALBUM_ROUTE) {
//            val interestsViewModel: InterestsViewModel = viewModel(
//                factory = InterestsViewModel.provideFactory(appContainer.interestsRepository)
//            )
//            InterestsRoute(
//                interestsViewModel = interestsViewModel,
//                isExpandedScreen = isExpandedScreen,
//                openDrawer = openDrawer
//            )
        }
    }
}
