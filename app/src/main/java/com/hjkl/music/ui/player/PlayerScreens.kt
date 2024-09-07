package com.hjkl.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ImageBackgroundColorScrim
import com.hjkl.music.ui.comm.PlayerPageMoreDialog
import com.hjkl.music.util.verticalGradientScrim
import com.hjkl.player.constant.PlayMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val defaultPageIndex = 1
private val pageCount = 3

@Composable
fun PlayerPages(
    uiState: PlayerUiState,
    onBackHandle: (KeyEvent?) -> Boolean,
    onValueChange: (Boolean, Long) -> Unit,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = defaultPageIndex) { pageCount }
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    fun resetPageIndex() {
        scope.launch {
            delay(500)
            pagerState.scrollToPage(defaultPageIndex)
        }
    }
    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onKeyEvent {
                "onKeyEvent".d()
                onBackHandle(it).also { resetPageIndex() }
            }, contentAlignment = Alignment.Center
    ) {
        PlayerBackground(
            song = uiState.curSong
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalGradientScrim(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                    startYPercentage = 1f,
                    endYPercentage = 0f
                )
                .systemBarsPadding()
                .padding(horizontal = 8.dp)
        ) {
            TopAppBar(pagerState = pagerState, onBackPress = {
                onBackHandle(null)
                resetPageIndex()
            })
            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {

                    }

                    1 -> {
                        PlayerContentRegular(
                            uiState = uiState,
                            onValueChange = onValueChange,
                            onPlaySwitchMode = onPlaySwitchMode,
                            onPlayPrev = onPlayPrev,
                            onPlayToggle = onPlayToggle,
                            onPlayNext = onPlayNext
                        )
                    }

                    2 -> {

                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

    }
}


@Composable
private fun PlayerBackground(
    song: Song?
) {
    if (song?.bitmap != null) {
        ImageBackgroundColorScrim(
            data = song.bitmap,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun TopAppBar(
    pagerState: PagerState,
    onBackPress: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth()) {
        IconButton(onClick = { onBackPress() }) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .weight(1F),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val indicatorColor = when {
                    pagerState.currentPage == iteration -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                        .size(8.dp),
                    contentAlignment = Alignment.Center
                ) {

                }
            }
        }

        IconButton(onClick = { showBottomSheet = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
    if (showBottomSheet) {
        PlayerPageMoreDialog(onDialogHide = { showBottomSheet = false })
    }
}

@Preview
@Composable
private fun PlayerPagesPreview() {
    PlayerPages(
        FakeDatas.songUiState.playerUiState,
        onBackHandle = { false },
        onValueChange = { i, f -> },
        onPlaySwitchMode = {},
        onPlayPrev = {},
        onPlayToggle = {},
        onPlayNext = {})
}

