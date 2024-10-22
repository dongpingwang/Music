package com.hjkl.music.ui.comm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Song
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.dialog.SongItemMoreDialog

@Composable
fun CommonSongItem(
    song: Song,
    order: Int? = null,
    onItemClicked: () -> Unit,
    onAddToQueue: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClicked()
            }
            .padding(vertical = 8.dp)
            .padding(start = 24.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (order == null) {
            AlbumImage(
                song.albumCoverPath ?: song.bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        } else {
            Text(
                text = "$order",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(start = 8.dp)
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
        IconButton(
            onClick = onAddToQueue
        ) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = {
                showBottomSheet = true
            }
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
@Composable
private fun CommonSongItemPreview() {
    CommonSongItem(song = FakeDatas.song, order = 1, onItemClicked = {}, onAddToQueue = {})
}