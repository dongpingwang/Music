package com.hjkl.music.ui.comm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.player.PlayerPages
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.constant.RepeatMode
import kotlinx.coroutines.launch

data class TopBarActions(val onDrawerClicked: () -> Unit)
data class BottomBarActions(
    val onPlayToggle: () -> Unit,
    val onScrollToNext: () -> Unit,
    val onScrollToPrevious: () -> Unit
)

data class PlayerActions(
    val onPlayerPageExpandChanged: (Boolean) -> Unit,
    val onPlayToggle: () -> Unit,
    val onSeekBarValueChange: (Boolean, Long) -> Unit,
    val onRepeatModeSwitch: (RepeatMode) -> Unit,
    val onShuffleModeEnable: (Boolean) -> Unit,
    val onPlayPrev: () -> Unit,
    val onPlayNext: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ScreenWithTopBottomBar(
    uiState: ViewModelState<T>,
    title: String,
    topBarActions: TopBarActions,
    bottomBarActions: BottomBarActions,
    playerActions: PlayerActions,
    content: @Composable ColumnScope.(BottomSheetScaffoldState) -> Unit
) {
    "ScreenWithTopBottomBar() call: ${uiState.shortLog()}".d()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )
    val snackbarHostState = remember { SnackbarHostState() }
    uiState.playerUiState.playerErrorMsgOnce?.let {
        scope.launch {
            snackbarHostState.showSnackbar(message = it, withDismissAction = true)
        }
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = title,
                onDrawerClicked = topBarActions.onDrawerClicked
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        sheetContent = {
            PlayerPages(
                uiState = uiState.playerUiState,
                onBackHandle = {
                    if (it == null) {
                        "手动点击返回按钮".d()
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                            playerActions.onPlayerPageExpandChanged(false)
                        }
                        return@PlayerPages false
                    }
                    "收到按键事件".d()
                    if (it.key == Key.Back) {
                        "收到返回按键事件".d()
                        val expanded =
                            scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                        if (expanded) {
                            scope.launch {
                                scaffoldState.bottomSheetState.hide()
                                playerActions.onPlayerPageExpandChanged(false)
                            }
                            "隐藏BottomSheet，拦截返回按键事件".d()
                            return@PlayerPages true
                        }
                    }
                    "不拦截按键事件".d()
                    false
                },
                onValueChange = playerActions.onSeekBarValueChange,
                onRepeatModeSwitch = playerActions.onRepeatModeSwitch,
                onShuffleModeEnable = playerActions.onShuffleModeEnable,
                onPlayPrev = playerActions.onPlayPrev,
                onPlayToggle = playerActions.onPlayToggle,
                onPlayNext = playerActions.onPlayNext
            )
        },
        sheetDragHandle = null,
        sheetPeekHeight = 0.dp,
        sheetMaxWidth = Dp.Infinity,
        sheetShape = BottomSheetDefaults.HiddenShape
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            content(scaffoldState)
            BottomMiniPlayer(
                uiState = uiState.playerUiState,
                onClick = {
                    scope.launch {
                        playerActions.onPlayerPageExpandChanged(true)
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                onTogglePlay = bottomBarActions.onPlayToggle,
                onScrollToNext = bottomBarActions.onScrollToNext,
                onScrollToPrevious = bottomBarActions.onScrollToPrevious
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ScreenWithTopBottomBarPreview() {
    ScreenWithTopBottomBar<Song>(
        uiState = FakeDatas.songUiState,
        title = "",
        topBarActions = TopBarActions(onDrawerClicked = { }),
        bottomBarActions = BottomBarActions(
            onPlayToggle = {},
            onScrollToNext = {},
            onScrollToPrevious = {}),
        playerActions = PlayerActions(
            onPlayerPageExpandChanged = {},
            onPlayToggle = {},
            onSeekBarValueChange = { i, f -> },
            onRepeatModeSwitch = {},
            onShuffleModeEnable = {},
            onPlayPrev = {},
            onPlayNext = {}
        )
    ) {

    }
}