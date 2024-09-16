package com.hjkl.music.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.model.Lyric
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.utils.DisplayUtil

@Composable
fun ExtSongInfoPage(
    uiState: PlayerUiState,
    lyric: Lyric?,
    onArtistClicked: (Artist) -> Unit,
    onAlbumClicked: (Album) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        SongInfoCard(
            song = uiState.curSong,
            lyric = lyric,
            onArtistClicked = onArtistClicked,
            onAlbumClicked = onAlbumClicked
        )
        AudioInfoCard(song = uiState.curSong)
    }
}

@Composable
private fun SongInfoCard(
    song: Song?,
    lyric: Lyric?,
    onArtistClicked: (Artist) -> Unit,
    onAlbumClicked: (Album) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Text(
            text = DisplayUtil.getDisplayTitle(song?.title),
            maxLines = 2,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    (song?.publishDate != null) -> stringResource(
                        id = R.string.summary_publish,
                        song.publishDate!!
                    )

                    else -> stringResource(id = R.string.summary_publish_unknown)
                },
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
//            Spacer(modifier = Modifier.weight(1F))
//            Row(verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.clickable {
//
//                }) {
//                Text(
//                    text = stringResource(R.string.song_detail),
//                    style = MaterialTheme.typography.titleSmall,
//                )
//                Image(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
//            }
        }
        HorizontalDivider(modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clickable {
                    song?.let {
                        onArtistClicked(it.getEmptyArtist())
                    }
                }
        ) {
            AlbumImage(
                data = song?.artCoverPath ?: song?.bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                placeHolderImage = R.drawable.default_artist_art,
                errorImage = R.drawable.default_artist_art
            )
            Text(
                text = stringResource(
                    R.string.summary_artist,
                    DisplayUtil.getDisplayArtist(song?.artist)
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clickable {
                    song?.let {
                        onAlbumClicked(it.getEmptyAlbum())
                    }
                }
        ) {
            AlbumImage(
                data = song?.albumCoverPath ?: song?.bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.medium),
                placeHolderImage = R.drawable.default_album_art,
                errorImage = R.drawable.default_album_art
            )
            Text(
                text = stringResource(
                    R.string.summary_album,
                    DisplayUtil.getDisplayAlbum(song?.album)
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .clickable {

                }
        ) {
            AlbumImage(
                data = song?.artCoverPath ?: song?.bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.medium),
                placeHolderImage = R.drawable.default_artist_art,
                errorImage = R.drawable.default_artist_art
            )
            Text(
                text = stringResource(
                    R.string.summary_production,
                    DisplayUtil.getDisplayComposer(song?.composer)
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .clickable {

                }
        ) {
            Image(
                imageVector = Icons.Outlined.TextSnippet, contentDescription = null, alpha = 0.6F,
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Text(
                text = DisplayUtil.getLyricType(song = song, lyric = lyric),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

    }
}

@Composable
private fun AudioInfoCard(song: Song?) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Text(
            text = stringResource(id = R.string.song_audio_track),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )
        Text(
            text = when {
                song != null -> {
                    DisplayUtil.getAudioTrackInfo(song)
                }

                else -> ""
            },
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.song_audio_decoder),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.song_audio_decoder_info),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun ExtSongInfoPagePreview() {
    ExtSongInfoPage(
        uiState = FakeDatas.playerUiState,
        lyric = null,
        onArtistClicked = {},
        onAlbumClicked = {})
}