package com.hjkl.music.ui.comm.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.comm.isSizeOne
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.data.Defaults
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.player.constant.RepeatMode
import kotlinx.coroutines.launch

data class PlaylistDialogActions(
    val onClearQueue: () -> Unit,
    val onRepeatModeSwitch: (RepeatMode) -> Unit,
    val onShuffleModeEnable: (Boolean) -> Unit,
    val onPlayIndex: (Int) -> Unit,
    val onRemoveIndex: (Int) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDialog(
    uiState: PlayerUiState,
    playlistDialogActions: PlaylistDialogActions = ActionHandler.playlistDialogAction,
    onDialogHide: () -> Unit = {}
) {
    var showClearQueueDialog by remember { mutableStateOf(false) }
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val screenHeightPx = displayMetrics.heightPixels
    val screenHeightDp = (screenHeightPx / displayMetrics.density).dp
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDialogHide()
        },
    ) {
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = uiState.curPlayIndex)
        Column(modifier = Modifier.height(screenHeightDp * 0.6F)) {
            ItemHeader(
                uiState = uiState,
                onClearQueue = { showClearQueueDialog = true },
                onRepeatModeSwitch = { playlistDialogActions.onRepeatModeSwitch(it) },
                onShuffleModeEnable = { playlistDialogActions.onShuffleModeEnable(it) })
            LazyColumn(state = listState) {
                itemsIndexed(uiState.playlist) { index, song ->
                    val curSongPlaying = uiState.curSong != null && uiState.curPlayIndex == index
                    Item(
                        isSongPlaying = curSongPlaying,
                        song = song,
                        onPlayIndex = { playlistDialogActions.onPlayIndex(index) },
                        onRemoveIndex = {
                            if (uiState.playlist.isSizeOne()) {
                                scope.launch {
                                    sheetState.hide()
                                    onDialogHide()
                                }
                            }
                            playlistDialogActions.onRemoveIndex(index)
                        })
                }
            }
        }
    }
    if (showClearQueueDialog) {
        EasyAlertDialog(
            dialogText = stringResource(id = R.string.dialog_clear_queue_content),
            confirmBtnText = stringResource(id = R.string.dialog_clear_confirm_txt),
            dismissBtnText = stringResource(id = R.string.dialog_clear_dismiss_txt),
            onConfirmation = {
                showClearQueueDialog = false
                scope.launch {
                    sheetState.hide()
                    onDialogHide()
                }
                playlistDialogActions.onClearQueue()
            },
            onDismissRequest = {
                showClearQueueDialog = false
            })
    }
}

@Composable
private fun ItemHeader(
    uiState: PlayerUiState,
    onClearQueue: () -> Unit,
    onRepeatModeSwitch: (RepeatMode) -> Unit,
    onShuffleModeEnable: (Boolean) -> Unit
) {
    val repeatMode = uiState.repeatMode
    val playlistSize = uiState.playlist.size
    val isRepeatModeOff = repeatMode == RepeatMode.REPEAT_MODE_OFF
    val repeatModeIcon = when (repeatMode) {
        RepeatMode.REPEAT_MODE_OFF -> Icons.Outlined.Repeat
        RepeatMode.REPEAT_MODE_ALL -> Icons.Outlined.Repeat
        RepeatMode.REPEAT_MODE_ONE -> Icons.Outlined.RepeatOne
        else -> Icons.Outlined.Repeat
    }
    val playModeTxt = when (repeatMode) {
        RepeatMode.REPEAT_MODE_OFF -> stringResource(id = R.string.play_mode_repeat_off)
        RepeatMode.REPEAT_MODE_ALL -> stringResource(id = R.string.play_mode_repeat)
        RepeatMode.REPEAT_MODE_ONE -> stringResource(id = R.string.play_mode_repeat_one)
        else -> stringResource(id = R.string.play_mode_repeat_off)
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "当前播放列表",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "（$playlistSize）", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.weight(1F))
            Image(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = null,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false)
                ) {
                    onClearQueue()
                })
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onRepeatModeSwitch(repeatMode)
                }) {
                Image(
                    imageVector = repeatModeIcon,
                    contentDescription = null,
                    alpha = if (isRepeatModeOff) 0.2F else 1F
                )
                Text(
                    text = playModeTxt,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .alpha(if (isRepeatModeOff) 0.2F else 1F)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onShuffleModeEnable(uiState.shuffled.not())
                    }) {
                Image(
                    imageVector = Icons.Outlined.Shuffle,
                    contentDescription = null,
                    alpha = if (uiState.shuffled.not()) 0.2F else 1F
                )
                Text(
                    text = if (uiState.shuffled) {
                        stringResource(id = R.string.play_mode_shuffle_enabled)
                    } else {
                        stringResource(id = R.string.play_mode_shuffle_off)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .alpha(if (uiState.shuffled.not()) 0.2F else 1F)
                )
            }
        }
    }
}


@Composable
private fun Item(
    isSongPlaying: Boolean,
    song: Song,
    onPlayIndex: () -> Unit,
    onRemoveIndex: () -> Unit
) {
    val title = song.title
    val artist = song.artist
    val highColor = MaterialTheme.colorScheme.primary
    var tms = MaterialTheme.typography.titleMedium.toSpanStyle()
    if (isSongPlaying) {
        tms = tms.copy(color = highColor)
    }
    var lss = MaterialTheme.typography.labelSmall.toSpanStyle()
    if (isSongPlaying) {
        lss = lss.copy(color = highColor)
    }
    Row(
        modifier = Modifier
            .clickable {
                onPlayIndex()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val desc = buildAnnotatedString {
            withStyle(
                style = tms
            ) {
                append(title)
            }
            withStyle(
                style = lss
            ) {
                append(" - $artist")
            }
        }
        Text(
            text = desc,
            minLines = 1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1F)
        )
        if (isSongPlaying) {
            Image(
                imageVector = Icons.Outlined.Equalizer, contentDescription = null,
                colorFilter = ColorFilter.tint(color = highColor),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Image(
            imageVector = Icons.Outlined.Clear,
            contentDescription = null,
            colorFilter = if (isSongPlaying) ColorFilter.tint(highColor) else null,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false)
            ) {
                onRemoveIndex()
            }
        )
    }
}

@Preview
@Composable
private fun ItemHeaderPreview() {
    ItemHeader(uiState = Defaults.defaultPlayerUiState,
        onClearQueue = {},
        onRepeatModeSwitch = {},
        onShuffleModeEnable = {})
}

@Preview
@Composable
private fun ItemPreview() {
    Item(isSongPlaying = false, song = FakeDatas.song, onPlayIndex = {}, onRemoveIndex = { })
}