package com.hjkl.music.ui.song

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.dialog.SongItemMoreDialog
import com.hjkl.music.ui.theme.MusicTheme
import com.hjkl.player.util.parseMillisTimeToMinutes

@Composable
fun HeaderSongItem(
    count: Int,
    onPlayAll: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp)
                ) {
                    onPlayAll()
                }
                .size(48.dp)
                .semantics { role = Role.Button }
        )
        Text(
            text = stringResource(id = R.string.formatter_play_all_desc, count),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1F)
        )
        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { onEdit() }
        ) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SongItem(
    isSongPlaying: Boolean,
    song: Song,
    onItemClicked: () -> Unit,
    onPlayClicked: () -> Unit,
    onAddToQueue: () -> Unit
) {

    Box(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        val bgColor = when {
            isSongPlaying -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainer
        }
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = bgColor
            ), onClick = { onItemClicked() }
        ) {

            SongItemHeader(song)
            SongItemFooter(
                isSongPlaying = isSongPlaying,
                song = song,
                onPlayClicked = onPlayClicked,
                onAddToQueue = onAddToQueue
            )
        }
    }
}

@Composable
private fun SongItemHeader(song: Song) {
    Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = song.title,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = song.artist,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        AlbumImage(
            song.bitmap,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    }
}

@Composable
private fun SongItemFooter(
    isSongPlaying: Boolean,
    song: Song,
    onPlayClicked: () -> Unit,
    onAddToQueue: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val playIcon = when {
        isSongPlaying -> Icons.Rounded.PauseCircleFilled
        else -> Icons.Rounded.PlayCircleFilled
    }
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = playIcon,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp)
                ) {
                    onPlayClicked()
                }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
        )

        Text(
            text = song.duration.parseMillisTimeToMinutes(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1F)
        )

        IconButton(
            onClick = {
                onAddToQueue()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = {
                showBottomSheet = true
            },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showBottomSheet) {
        SongItemMoreDialog(onDialogHide = { showBottomSheet = false })
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SongItemPreview() {
    MusicTheme {
        Surface {
            SongItem(
                isSongPlaying = true,
                song = FakeDatas.song,
                onItemClicked = {},
                onPlayClicked = {},
                onAddToQueue = {})
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HeaderSongItemPreview() {
    MusicTheme {
        Surface {
            HeaderSongItem(4, onPlayAll = {}, onEdit = {})
        }
    }
}

