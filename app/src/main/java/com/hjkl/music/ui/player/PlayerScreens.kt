package com.hjkl.music.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.comm.d
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.SongUiState
import com.hjkl.music.ui.comm.asSuccess
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.util.parseMillisTimeToMmSs


@Composable
fun PlayerPage(
    uiState: SongUiState,
    onBackHandle: (KeyEvent?) -> Boolean,
    onValueChange: (Boolean, Float) -> Unit,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    Box(modifier = Modifier
        .fillMaxSize()
        .focusRequester(focusRequester)
        .onKeyEvent {
            "onKeyEvent".d()
            onBackHandle(it)
        }) {
        val pagerState = rememberPagerState(initialPage = 1) { 3 }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {

                }

                1 -> {
                    PlaySongInfoPage(
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
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 32.dp)
        ) {
            IconButton(onClick = { onBackHandle(null) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_arrow_down_24px),
                    contentDescription = null
                )
            }
            Row(modifier = Modifier.align(alignment = Alignment.Center)) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp),
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
            }

        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


// 播放信息页面
@Composable
private fun PlaySongInfoPage(
    uiState: SongUiState,
    onValueChange: (Boolean, Float) -> Unit,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 32.dp, end = 32.dp)
    ) {
        val bitmap = uiState.asSuccess().curSong?.bitmap
        val title = uiState.asSuccess().curSong?.title ?: "未知歌曲"
        val artist = uiState.asSuccess().curSong?.artist ?: "未知歌手"
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(360.dp)
                .background(color = Color.LightGray)
        ) {
            if (bitmap == null) {
                Image(
                    painter = painterResource(id = R.drawable.music_note_40px),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(text = title, modifier = Modifier.padding(top = 16.dp))

        Text(text = artist, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.weight(1F))
        var progressRatio = 0F
        uiState.asSuccess().curSong?.let {
            progressRatio = uiState.asSuccess().progressInMs.toFloat() / it.duration
        }
        var progressRatioUI = progressRatio
        Slider(
            value = progressRatio,
            onValueChange = {
                progressRatioUI = it
                onValueChange(true, progressRatioUI)
            },
            onValueChangeFinished = { onValueChange(false, progressRatioUI) },
            modifier = Modifier.padding()
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)) {
            Text(text = uiState.asSuccess().progressInMs.parseMillisTimeToMmSs())
            Text(
                text = (uiState.asSuccess().curSong?.duration ?: 0L).parseMillisTimeToMmSs(),
                modifier = Modifier.align(
                    Alignment.TopEnd
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            val playModeRes = when (uiState.asSuccess().playMode) {
                PlayMode.LIST -> R.drawable.repeat_24px
                PlayMode.REPEAT_ONE -> R.drawable.repeat_one_24px
                PlayMode.SHUFFLE -> R.drawable.shuffle_24px
                else -> R.drawable.repeat_24px
            }
            val playStateRes =
                if (uiState.asSuccess().isPlaying) R.drawable.pause_24px else R.drawable.play_arrow_24px
            IconButton(
                onClick = { onPlaySwitchMode(uiState.asSuccess().playMode) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = playModeRes),
                    contentDescription = null
                )
            }

            Row(modifier = Modifier.align(Alignment.Center)) {
                IconButton(onClick = { onPlayPrev() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.skip_previous_24px),
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = { onPlayToggle() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = playStateRes),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { onPlayNext() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.skip_next_24px),
                        contentDescription = null
                    )
                }
            }

            IconButton(onClick = { }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.queue_music_24px),
                    contentDescription = null
                )
            }
        }
        Spacer(
            modifier = Modifier.padding(bottom = 64.dp)
        )
    }
}

@Preview
@Composable
private fun PlayerPagePreview() {
    PlayerPage(
        uiState = FakeDatas.songUiState,
        onBackHandle = { false },
        onValueChange = { isUserSeeking, progressRatio -> },
        onPlaySwitchMode = { playMode -> },
        onPlayPrev = {},
        onPlayToggle = {},
        onPlayNext = {}
    )
}