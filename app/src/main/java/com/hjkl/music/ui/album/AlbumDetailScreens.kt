package com.hjkl.music.ui.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.comm.onTrue
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.bar.TitleAnimatedVisibleTopBar
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.CommonSongItem
import com.hjkl.music.utils.DisplayUtil
import com.hjkl.player.util.parseMillisTimeToMmSs

@Composable
fun AlbumDetailScreens(
    albumId: Int
) {
    val albumViewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModel.provideFactory()
    )
    val uiState by albumViewModel.uiState.collectAsStateWithLifecycle()

    val actionHandler = ActionHandler.get()

    val curAlbum = uiState.datas.find { it.id == albumId }
    val otherAlbums =
        uiState.datas.filter { it.id != albumId && it.getArtist() == curAlbum?.getArtist() }
    if (curAlbum != null) {
        AlbumDetailScreens(
            album = curAlbum,
            otherAlbums = otherAlbums,
            onBackClicked = actionHandler.navigationActions.popBackStack,
            onOpenArtist = actionHandler.navigationActions.navigateToArtistDetail,
            onOpenOtherAlbum = actionHandler.navigationActions.navigateToAlbumDetail,
            onItemClicked = {
                actionHandler.itemActions.onItemClicked(curAlbum.getSongs(), it).onTrue {
                    actionHandler.navigationActions.navigateToPlayer()
                }
            },
            onAddToQueue = actionHandler.itemActions.onAddToQueue
        )
    }

}

@Composable
fun AlbumDetailScreens(
    album: Album,
    otherAlbums: List<Album>,
    onBackClicked: () -> Unit,
    onOpenArtist: (Artist) -> Unit,
    onOpenOtherAlbum: (Album) -> Unit,
    onItemClicked: (Int) -> Unit,
    onAddToQueue: (Song) -> Unit
) {
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
            title = album.name,
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
                ItemHeader(album = album, onOpenArtist = onOpenArtist)
            }
            itemsIndexed(album.getSongs()) { index, song ->
                CommonSongItem(
                    order = index + 1,
                    song = song,
                    onItemClicked = {
                        onItemClicked(index)
                    },
                    onAddToQueue = {
                        onAddToQueue(song)
                    })
            }
            if (otherAlbums.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(64.dp))
                }
                item {
                    AlbumRow(otherAlbums = otherAlbums, onOpenOtherAlbum = onOpenOtherAlbum)
                }
            }
        }
    }
}

@Composable
private fun ItemHeader(album: Album, onOpenArtist: (Artist) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row {
            AlbumImage(
                data = album.getAlbumArtBitmap(),
                contentDescription = null,
                placeHolderImage = R.drawable.default_album_art,
                errorImage = R.drawable.default_album_art,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(128.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = album.name,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.clickable {
                    onOpenArtist(album.getEmptyArtist())
                }) {
                    AlbumImage(
                        data = album.getAlbumArtBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        placeHolderImage = R.drawable.default_artist_art,
                        errorImage = R.drawable.default_artist_art
                    )
                    Text(
                        text = DisplayUtil.getDisplayArtist(album.getArtist()),
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 36.dp, max = 40.dp),
        ) {
            Column(
                modifier = Modifier.weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${album.getSongCount()}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "歌曲", style = MaterialTheme.typography.labelSmall)
            }
            Column(
                modifier = Modifier.weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = album.getTotalDuration().parseMillisTimeToMmSs(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "时长", style = MaterialTheme.typography.labelSmall)
            }
            Column(
                modifier = Modifier.weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = DisplayUtil.getDisplayYear(album.getYear()),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "年份", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    Spacer(
        modifier = Modifier
            .height(8.dp)
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun AlbumRow(
    otherAlbums: List<Album>,
    onOpenOtherAlbum: (Album) -> Unit
) {
    Text(
        text = "该歌手的其他专辑",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    LazyRow(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        itemsIndexed(otherAlbums) { index, album ->
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.clickable {
                    onOpenOtherAlbum(album)
                }) {
                AlbumImage(
                    data = album.getAlbumArtBitmap(),
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
}

@Preview
@Composable
private fun AlbumDetailScreensPreview() {
    AlbumDetailScreens(
        album = FakeDatas.album,
        otherAlbums = FakeDatas.albums,
        onBackClicked = {},
        onOpenOtherAlbum = {},
        onOpenArtist = {},
        onItemClicked = {},
        onAddToQueue = {})
}