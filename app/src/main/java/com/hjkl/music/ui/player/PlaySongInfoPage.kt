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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hjkl.comm.getOrDefault
import com.hjkl.entity.Song
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.PlaylistDialog
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.util.parseMillisTimeToMmSs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerContentRegular(
    uiState: PlayerUiState,
    onValueChange: (Boolean, Long) -> Unit,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1F))
        PlayerImage(
            song = uiState.curSong,
            modifier = Modifier.weight(10F)
        )
        Spacer(modifier = Modifier.height(32.dp))
        SongDescription(
            uiState.curSong?.title.getOrDefault("未知歌曲"),
            uiState.curSong?.artist.getOrDefault("未知歌手")
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(10F)
        ) {
            PlayerSlider(
                progressInMillis = uiState.progressInMs,
                durationInMillis = uiState.curSong?.duration.getOrDefault(0L),
                onValueChange = onValueChange,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PlayerButtons(
                isPlaying = uiState.isPlaying,
                playMode = uiState.playMode,
                onPlaySwitchMode = onPlaySwitchMode,
                onPlayPrev = onPlayPrev,
                onPlayToggle = onPlayToggle,
                onPlayNext = onPlayNext,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1F))
    }
}


@Composable
private fun PlayerImage(
    song: Song?,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        data = song?.bitmap,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
private fun SongDescription(
    title: String,
    artist: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Text(
        text = title,
        style = titleTextStyle,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.basicMarquee()
    )
    Text(
        text = artist,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
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
    isPlaying: Boolean,
    playMode: PlayMode,
    onPlaySwitchMode: (PlayMode) -> Unit,
    onPlayPrev: () -> Unit,
    onPlayToggle: () -> Unit,
    onPlayNext: () -> Unit,
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val sideButtonsModifier = Modifier
            .size(sideButtonSize)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val primaryButtonModifier = Modifier
            .size(playerButtonSize)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val playModeIcon = when (playMode) {
            PlayMode.LIST -> Icons.Default.Repeat
            PlayMode.REPEAT_ONE -> Icons.Default.RepeatOne
            PlayMode.SHUFFLE -> Icons.Default.Shuffle
            else -> Icons.Default.Repeat
        }

        Image(
            imageVector = playModeIcon,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = sideButtonsModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = sideButtonSize / 2)
            ) {
                onPlaySwitchMode(playMode)
            }
        )
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = sideButtonsModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = sideButtonSize / 2)
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
                    indication = ripple(bounded = false, radius = playerButtonSize / 2)
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
                    indication = ripple(bounded = false, radius = sideButtonSize / 2)
                ) {
                    onPlayNext()
                }
        )

        Image(
            imageVector = Icons.Filled.QueueMusic,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = sideButtonsModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = sideButtonSize / 2)
            ) {
                showBottomSheet = true
            }
        )
    }

    if (showBottomSheet) {
        PlaylistDialog(onDialogHide = { showBottomSheet = false })
    }

}

@Preview
@Composable
fun PlayerContentRegularPreview() {
    PlayerContentRegular(
        uiState = FakeDatas.songUiState.playerUiState,
        onValueChange = { isUserSeeking, progressInMillis -> },
        onPlaySwitchMode = { playMode -> },
        onPlayPrev = {},
        onPlayToggle = {},
        onPlayNext = {}
    )
}


