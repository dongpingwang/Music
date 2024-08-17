package com.hjkl.music.ui.song

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.theme.MusicTheme

@Composable
fun HeaderSongItem(
    count: Int,
    onPlayAll: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { onPlayAll() },
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp)
                .size(32.dp)

        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.playlist_play_24px),
                contentDescription = null,
            )
        }
        Text(
            text = stringResource(id = R.string.play_all_desc, count),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1F)
                .padding(vertical = 20.dp)
        )
        IconButton(
            onClick = { onMoreClick() },
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 16.dp)
                .size(32.dp)

        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.playlist_add_check_24px),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onItemClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onItemClick() })

    ) {
        if (song.bitmap != null) {
            Image(
                bitmap = song.bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
            )
        } else {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.audio_file_24px),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceDim)
            )
        }
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(
            onClick = { onMoreClick() },
            modifier = Modifier
                .padding(vertical = 10.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
            )
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SongItemPreview() {
    MusicTheme {
        Surface {
            SongItem(song = FakeDatas.song, onItemClick = {}, onMoreClick = {})
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HeaderSongItemPreview() {
    MusicTheme {
        Surface {
            HeaderSongItem(4, onPlayAll = {}, onMoreClick = {})
        }
    }
}

