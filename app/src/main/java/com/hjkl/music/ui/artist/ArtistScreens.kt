package com.hjkl.music.ui.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Artist
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.ArtistUiState
import com.hjkl.music.ui.comm.BottomBarActions
import com.hjkl.music.ui.comm.PlayerActions
import com.hjkl.music.ui.comm.ScreenWithTopBottomBar
import com.hjkl.music.ui.comm.TopBarActions


@Composable
fun ArtistScreen(
    onDrawerClicked: () -> Unit
) {
    val artistViewModel: ArtistViewModel = viewModel(
        factory = ArtistViewModel.provideFactory()
    )
    val uiState by artistViewModel.uiState.collectAsStateWithLifecycle()
    ArtistScreen(
        uiState = uiState,
        topBarActions = TopBarActions(onDrawerClicked = onDrawerClicked),
        bottomBarActions = ActionHandler.get().bottomBarActions,
        playerActions = ActionHandler.get().playerActions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    uiState: ArtistUiState,
    topBarActions: TopBarActions,
    bottomBarActions: BottomBarActions,
    playerActions: PlayerActions
) {
    ScreenWithTopBottomBar(
        uiState = uiState,
        title = stringResource(id = R.string.artist_title),
        topBarActions = topBarActions,
        bottomBarActions = bottomBarActions,
        playerActions = playerActions
    ) {
        LazyColumn(modifier = Modifier.weight(1F)) {
            itemsIndexed(uiState.datas) { index, artist ->
                ArtistItem(artist = artist, onItemClicked = {})
            }
        }
    }
}

@Composable
private fun ArtistItem(
    artist: Artist,
    onItemClicked: () -> Unit,

    ) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClicked() }) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            AlbumImage(
                data = artist.getAlbumArtBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                placeHolderImage = R.drawable.default_artist_art,
                errorImage = R.drawable.default_artist_art
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                val albumDesc = stringResource(
                    id = R.string.formatter_album_count_desc,
                    artist.getSongCount()
                )
                Text(
                    text = artist.name,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = albumDesc,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ArtistScreenPreview() {
    ArtistScreen(
        uiState = FakeDatas.artistUiState,
        topBarActions = TopBarActions(onDrawerClicked = {}),
        bottomBarActions = BottomBarActions(
            onPlayToggle = {},
            onScrollToNext = {},
            onScrollToPrevious = {}),
        playerActions = PlayerActions(
            onPlayerPageExpandChanged = {},
            onPlayToggle = {},
            onSeekBarValueChange = { i, f -> },
            onRepeatModeSwitch = {},
            onShuffleModeEnable = {},
            onPlayPrev = {},
            onPlayNext = {}
        )
    )
}

@Preview
@Composable
private fun AlbumItemPreview() {
    ArtistItem(artist = FakeDatas.artist, onItemClicked = { })
}