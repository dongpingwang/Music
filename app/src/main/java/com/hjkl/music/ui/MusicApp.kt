package com.hjkl.music.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.ui.bar.BottomMiniPlayer
import com.hjkl.music.ui.bar.PlayerSnackbar
import com.hjkl.music.ui.player.PlayerViewModel
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.home.HomeScreenDrawerContents
import com.hjkl.music.ui.home.Screen
import com.hjkl.music.ui.home.drawerWidth
import com.hjkl.music.ui.theme.MusicTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicApp(
) {
    var showBottomPlayer by remember { mutableStateOf(true) }
    var selectedScreen by remember { mutableStateOf(Screen.Home) }

    val actionHandler = ActionHandler.get()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }
    actionHandler.inject(navigationActions)

    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(key1 = backStackEntry) {
        val currentRoute = backStackEntry?.destination?.route
        showBottomPlayer = currentRoute != Destinations.PLAYER_ROUTE
    }
    val density = LocalDensity.current
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    val anchors = DraggableAnchors {
        DrawerState.OPEN at drawerWidthPx
        DrawerState.CLOSE at 0F
    }

    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DrawerState.CLOSE,
            anchors = anchors,
            positionalThreshold = { d: Float ->
                d * 0.35F
            },
            velocityThreshold = { 0.25F },
            snapAnimationSpec = tween(),
            decayAnimationSpec = exponentialDecay()
        )
    }

    val playerViewModel: PlayerViewModel = viewModel(
        factory = PlayerViewModel.provideFactory()
    )
    val playerUiState by playerViewModel.playerManager.playerUiState.collectAsStateWithLifecycle()


    MusicTheme {
        Column {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .anchoredDraggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        startDragImmediately = false
                    )
            ) {
                HomeScreenDrawerContents(modifier = Modifier.offset {
                    IntOffset(
                        -drawerWidthPx.toInt() + if (draggableState.offset.isNaN()) 0 else draggableState.offset.roundToInt(),
                        0
                    )
                }, selectedScreen) {
                    selectedScreen = it
                    if (backStackEntry?.destination?.route != Destinations.HOME_ROUTE) {
                        navigationActions.navigateToHome()
                    }
                    if (draggableState.currentValue == DrawerState.OPEN) {
                        scope.launch { draggableState.animateTo(DrawerState.CLOSE) }
                    }
                }
                Box(modifier = Modifier.offset {
                    IntOffset(
                        if (draggableState.offset.isNaN()) 0 else draggableState.offset.roundToInt(),
                        0
                    )
                }) {
                    ScreenContents(drawScreen = selectedScreen,
                        navigationActions = navigationActions,
                        playerUiState = playerUiState,
                        onDrawerClicked = {
                            if (draggableState.currentValue == DrawerState.OPEN) {
                                scope.launch { draggableState.animateTo(DrawerState.CLOSE) }
                            } else {
                                scope.launch { draggableState.animateTo(DrawerState.OPEN) }
                            }
                        }
                    )
                }
                PlayerSnackbar(uiState = playerUiState)
            }

            AnimatedVisibility(visible = showBottomPlayer) {
                BottomMiniPlayer(
                    uiState = playerUiState,
                    onTogglePlay = actionHandler.bottomBarActions.onPlayToggle,
                    onClick = {
                        if (draggableState.currentValue == DrawerState.OPEN) {
                            scope.launch { draggableState.animateTo(DrawerState.CLOSE) }
                        }
                        showBottomPlayer = false
                        navigationActions.navigateToPlayer()
                    })
            }
        }
        BackHandler(enabled = draggableState.currentValue == DrawerState.OPEN) {
            scope.launch { draggableState.animateTo(DrawerState.CLOSE) }
        }
    }
}

@Composable
private fun ScreenContents(
    drawScreen: Screen,
    navigationActions: NavigationActions,
    playerUiState: PlayerUiState,
    onDrawerClicked: () -> Unit,
) {
    MusicNavGraph(
        drawScreen = drawScreen,
        navigationActions = navigationActions,
        playerUiState = playerUiState,
        onDrawerClicked = onDrawerClicked
    )
}

private enum class DrawerState {
    OPEN, CLOSE
}

