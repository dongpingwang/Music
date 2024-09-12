package com.hjkl.music.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.comm.getOrDefault
import com.hjkl.entity.Song
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.dialog.PlaylistDialog
import com.hjkl.player.constant.RepeatMode
import com.hjkl.player.util.parseMillisTimeToMmSs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerContentRegular(
    uiState: PlayerUiState,
    onValueChange: (Boolean, Long) -> Unit,
    onRepeatModeSwitch: (RepeatMode) -> Unit,
    onShuffleModeEnable: (Boolean) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1F))
        PlayerImage(
            song = uiState.curSong
        )
        Spacer(modifier = Modifier.height(4.dp))
        SongDescription(uiState = uiState, onToggleCollect = {})
        Spacer(modifier = Modifier.height(148.dp))
        PlayerSlider(
            progressInMillis = uiState.progressInMs,
            durationInMillis = uiState.curSong?.duration.getOrDefault(0L),
            onValueChange = onValueChange,
        )
        Spacer(modifier = Modifier.height(4.dp))
        PlayerButtons(
            uiState = uiState,
            onRepeatModeSwitch = onRepeatModeSwitch,
            onShuffleModeEnable = onShuffleModeEnable,
            onPlayPrev = onPlayPrev,
            onPlayToggle = onPlayToggle,
            onPlayNext = onPlayNext
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun PlayerImage(
    song: Song?,
) {
    AlbumImage(
        data = song?.bitmap,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .padding(horizontal = 8.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium),
        placeHolderImage = null
    )
}

@Composable
private fun SongDescription(
    uiState: PlayerUiState,
    onToggleCollect: () -> Unit
) {
    val title = uiState.curSong?.title.getOrDefault("未知歌曲")
    val artist = uiState.curSong?.artist.getOrDefault("未知歌手")
    var showBottomSheet by remember { mutableStateOf(false) }
    val sideButtonsModifier = Modifier
        .size(48.dp)
        .background(
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = CircleShape
        )
        .semantics { role = Role.Button }
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
                    .padding(top = 8.dp)
            )
        }
        Row(modifier = Modifier.padding(start = 8.dp)) {
            Image(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = sideButtonsModifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 48.dp / 2)
                ) {
                    onToggleCollect()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                imageVector = Icons.Filled.QueueMusic,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = sideButtonsModifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 48.dp / 2)
                ) {
                    showBottomSheet = true
                }
            )
        }

    }
    if (showBottomSheet) {
        PlaylistDialog(uiState = uiState, onDialogHide = { showBottomSheet = false })
    }
}


@Composable
private fun PlayerSlider(
    progressInMillis: Long,
    durationInMillis: Long,
    onValueChange: (Boolean, Long) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        var sliderValue by remember(progressInMillis) { mutableLongStateOf(progressInMillis) }
        val maxRange = durationInMillis.milliseconds.inWholeSeconds.toFloat()
        Slider(
            value = sliderValue.milliseconds.inWholeSeconds.toFloat(),
            valueRange = 0F..maxRange,
            onValueChange = {
                sliderValue = it.toLong().seconds.inWholeMilliseconds
                onValueChange(true, sliderValue)
            },
            onValueChangeFinished = { onValueChange(false, sliderValue) },
        )
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = sliderValue.parseMillisTimeToMmSs(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = durationInMillis.parseMillisTimeToMmSs(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlayerButtons(
    uiState: PlayerUiState,
    onRepeatModeSwitch: (RepeatMode) -> Unit,
    onShuffleModeEnable: (Boolean) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit,
) {
    val repeatMode = uiState.repeatMode
    val isPlaying = uiState.isPlaying

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val sideButtonsModifier = Modifier
            .size(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val primaryButtonModifier = Modifier
            .size(72.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val isRepeatModeOff = repeatMode == RepeatMode.REPEAT_MODE_OFF
        val repeatModeIcon = when (repeatMode) {
            RepeatMode.REPEAT_MODE_OFF -> Icons.Default.Repeat
            RepeatMode.REPEAT_MODE_ALL -> Icons.Default.Repeat
            RepeatMode.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
            else -> Icons.Default.Repeat
        }

        Image(
            imageVector = repeatModeIcon,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            alpha = if (isRepeatModeOff) 0.2F else 1F,
            modifier = sideButtonsModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 48.dp / 2)
            ) {
                onRepeatModeSwitch(repeatMode)
            }
        )
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = sideButtonsModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 48.dp / 2)
            ) {
                onPlayPrev()
            }
        )

        Image(
            imageVector = when {
                isPlaying -> Icons.Outlined.Pause
                else -> Icons.Outlined.PlayArrow
            },
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = primaryButtonModifier
                .padding(8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 72.dp / 2)
                ) {
                    onPlayToggle()
                }
        )

        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = sideButtonsModifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 48.dp / 2)
                ) {
                    onPlayNext()
                }
        )
        Image(
            imageVector = Icons.Default.Shuffle,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            alpha = if (uiState.shuffled.not()) 0.2F else 1F,
            modifier = sideButtonsModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 48.dp / 2)
            ) {
                onShuffleModeEnable(uiState.shuffled.not())
            }
        )
    }
}

@Preview
@Composable
fun PlayerContentRegularPreview() {
    PlayerContentRegular(
        uiState = FakeDatas.songUiState.playerUiState,
        onValueChange = { isUserSeeking, progressInMillis -> },
        onRepeatModeSwitch = { },
        onShuffleModeEnable = {},
        onPlayPrev = {},
        onPlayToggle = {},
        onPlayNext = {}
    )
}


