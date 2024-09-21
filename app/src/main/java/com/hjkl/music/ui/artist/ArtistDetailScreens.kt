package com.hjkl.music.ui.artist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.bar.TitleAnimatedVisibleTopBar
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.CommonSongItem

@Composable
fun ArtistDetailScreens(artistId: Int) {
    val artistViewModel: ArtistViewModel = viewModel(
        factory = ArtistViewModel.provideFactory()
    )
    val uiState by artistViewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = ActionHandler
    val curArtist = uiState.datas.find { it.id == artistId }
    curArtist?.let {
        ArtistDetailScreens(
            artist = it,
            onBackClicked = actionHandler.navigationActions.popBackStack,
            onOpenAlbum = actionHandler.navigationActions.navigateToAlbumDetail,
            onItemClicked = { actionHandler.itemActions.onItemClicked(curArtist.getSongs(), it) },
            onAddToQueue = actionHandler.itemActions.onAddToQueue
        )
    }
}


@Composable
fun ArtistDetailScreens(
    artist: Artist,
    onBackClicked: () -> Unit,
    onOpenAlbum: (Album) -> Unit,
    onItemClicked: (Int) -> Unit,
    onAddToQueue: (Song) -> Unit
) {
    var songListExpandState by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val showTopBarTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TitleAnimatedVisibleTopBar(
            title = artist.name,
            visible = showTopBarTitle,
            onBackClicked = onBackClicked
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(top = 64.dp)
        ) {
            item {
                ItemHeader(artist = artist)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                AlbumRow(albums = artist.getAlbums(), onOpenAlbum = onOpenAlbum)
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.summary_song_count,
                            artist.getSongCount()
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = if (songListExpandState) {
                            stringResource(id = R.string.collapse)
                        } else {
                            stringResource(id = R.string.expand)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            songListExpandState = !songListExpandState
                        }
                    )
                }
            }

            if (songListExpandState) {
                itemsIndexed(artist.getSongs()) { index, song ->
                    CommonSongItem(
                        song = song,
                        onItemClicked = { onItemClicked(index) },
                        onAddToQueue = { onAddToQueue(song) })
                }
            }
        }
    }
}

@Composable
private fun ItemHeader(artist: Artist) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumImage(
            data = artist.getArtCoverPath() ?: artist.getAlbumArtBitmap(),
            contentDescription = null,
            placeHolderImage = R.drawable.default_artist_art,
            errorImage = R.drawable.default_artist_art,
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
        )
        Text(
            text = artist.name,
            maxLines = 2,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = stringResource(
                id = R.string.summary_song_count_alum_count,
                artist.getSongCount(),
                artist.getAlbumsCount()
            ),
            maxLines = 2,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
    Spacer(
        modifier = Modifier
            .height(8.dp)
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun AlbumRow(
    albums: List<Album>,
    onOpenAlbum: (Album) -> Unit
) {
    var expandState by remember { mutableStateOf(true) }
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = R.string.summary_alum_count, albums.size),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(
            text = if (expandState) {
                stringResource(id = R.string.collapse)
            } else {
                stringResource(id = R.string.expand)
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable {
                expandState = !expandState
            }
        )
    }
    AnimatedVisibility(visible = expandState) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            LazyRow {
                itemsIndexed(albums) { index, album ->
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.clickable {
                            onOpenAlbum(album)
                        }) {
                        AlbumImage(
                            data = album.getAlbumCoverPath() ?: album.getAlbumArtBitmap(),
                            contentDescription = null,
                            placeHolderImage = R.drawable.default_album_art,
                            errorImage = R.drawable.default_album_art,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(128.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                        Text(
                            text = album.name,
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .widthIn(max = 128.dp)
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

}

@Preview
@Composable
private fun ArtistDetailScreensPreview() {
    ArtistDetailScreens(artist = FakeDatas.artist,
        onBackClicked = {},
        onOpenAlbum = {},
        onItemClicked = {},
        onAddToQueue = {})
}