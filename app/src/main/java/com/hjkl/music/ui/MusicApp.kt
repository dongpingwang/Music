package com.hjkl.music.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.hjkl.comm.ResUtil
import com.hjkl.comm.d
import com.hjkl.music.R
import com.hjkl.music.ui.album.AlbumScreen
import com.hjkl.music.ui.song.SongScreen
import com.hjkl.music.ui.theme.MusicTheme
import kotlinx.coroutines.launch

@Composable
fun MusicApp(
) {
    MusicTheme {
        Surface {

            var drawerState by remember {
                mutableStateOf(DrawerState.Closed)
            }
            var screenState by remember {
                mutableStateOf(Screen.Home)
            }
            val translationX = remember {
                Animatable(0f)
            }

            val drawerWidth = with(LocalDensity.current) {
                DrawerWidth.toPx()
            }
            translationX.updateBounds(0f, drawerWidth)
            val coroutineScope = rememberCoroutineScope()
            val decay = rememberSplineBasedDecay<Float>()

            val draggableState = rememberDraggableState(onDelta = { dragAmount ->
                coroutineScope.launch {
                    translationX.snapTo(translationX.value + dragAmount)
                }
            })

            fun toggleDrawerState() {
                coroutineScope.launch {
                    if (drawerState == DrawerState.Open) {
                        translationX.animateTo(0f)
                    } else {
                        translationX.animateTo(drawerWidth)
                    }
                    drawerState = if (drawerState == DrawerState.Open) {
                        DrawerState.Closed
                    } else {
                        DrawerState.Open
                    }
                }
            }
            HomeScreenDrawerContents(
                selectedScreen = screenState,
                onScreenSelected = { screen ->
                    screenState = screen
                })

            ScreenContents(
                selectedScreen = screenState,
                onDrawerClicked = {
                    toggleDrawerState()
                },
                onBackHandle = {
                    if (it.key == Key.Back) {
                        if (drawerState == DrawerState.Open) {
                            "隐藏AppDrawer，拦截返回按键事件".d()
                            toggleDrawerState()
                            return@ScreenContents true
                        }
                    }
                    return@ScreenContents false
                },
                modifier = Modifier
                    .graphicsLayer {
                        this.translationX = translationX.value
                        val scale = lerp(1f, 0.8f, translationX.value / drawerWidth)
                        this.scaleX = scale
                        this.scaleY = scale
                        val roundedCorners =
                            lerp(0f, 32.dp.toPx(), translationX.value / drawerWidth)
                        this.shape = RoundedCornerShape(roundedCorners)
                        this.clip = true
                        this.shadowElevation = 32f
                    }
                    .draggable(draggableState, Orientation.Horizontal, onDragStopped = { velocity ->
                        val targetOffsetX = decay.calculateTargetValue(
                            translationX.value, velocity
                        )
                        coroutineScope.launch {
                            val actualTargetX = if (targetOffsetX > drawerWidth * 0.5) {
                                drawerWidth
                            } else {
                                0f
                            }
                            val targetDifference = (actualTargetX - targetOffsetX)
                            val canReachTargetWithDecay =
                                (targetOffsetX > actualTargetX && velocity > 0f && targetDifference > 0f) || (targetOffsetX < actualTargetX && velocity < 0 && targetDifference < 0f)
                            if (canReachTargetWithDecay) {
                                translationX.animateDecay(
                                    initialVelocity = velocity, animationSpec = decay
                                )
                            } else {
                                translationX.animateTo(
                                    actualTargetX, initialVelocity = velocity
                                )
                            }
                            drawerState = if (actualTargetX == drawerWidth) {
                                DrawerState.Open
                            } else {
                                DrawerState.Closed
                            }
                        }
                    })
            )
        }
    }
}

@Composable
private fun ScreenContents(
    selectedScreen: Screen,
    onDrawerClicked: () -> Unit,
    onBackHandle: (KeyEvent) -> Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Box(
        modifier
            .focusRequester(focusRequester)
            .onKeyEvent {
                "onKeyEvent: $it".d()
                onBackHandle(it)
            }) {
        when (selectedScreen) {
            Screen.Home -> {
                SongScreen(onDrawerClicked = onDrawerClicked)
            }

            Screen.ALBUM -> {
                AlbumScreen(onDrawerClicked = onDrawerClicked)
            }

            Screen.ARTIST -> {}


            Screen.FOLDER -> {}

            Screen.FAVORITE -> {}

            Screen.MYLIST -> {}

            Screen.SETTING -> {}
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun HomeScreenDrawerContents(
    selectedScreen: Screen, onScreenSelected: (Screen) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        Screen.entries.forEach {
            NavigationDrawerItem(
                label = { Text(it.text) },
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.text)
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.White),
                selected = selectedScreen == it,
                onClick = {
                    onScreenSelected(it)
                }
            )
        }
    }
}

private enum class DrawerState {
    Open, Closed
}

private val DrawerWidth = 285.dp

private enum class Screen(val text: String, val icon: ImageVector) {
    Home(ResUtil.getString(R.string.song_title), Icons.Default.Home),
    ALBUM(ResUtil.getString(R.string.album_title), Icons.Default.Album),
    ARTIST(ResUtil.getString(R.string.artist_title), Icons.Default.PeopleAlt),
    FOLDER(ResUtil.getString(R.string.folder_title), Icons.Default.Folder),
    FAVORITE(ResUtil.getString(R.string.folder_title), Icons.Default.Favorite),
    MYLIST(ResUtil.getString(R.string.mylist_title), Icons.Default.ListAlt),
    SETTING(ResUtil.getString(R.string.setting_title), Icons.Default.Settings)
}
