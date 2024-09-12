package com.hjkl.music.ui.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Album
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.AlbumUiState
import com.hjkl.music.ui.comm.BottomBarActions
import com.hjkl.music.ui.comm.PlayerActions
import com.hjkl.music.ui.comm.ScreenWithTopBottomBar
import com.hjkl.music.ui.comm.TopBarActions

@Composable
fun AlbumScreen(
    onDrawerClicked: () -> Unit
) {
    val albumViewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModel.provideFactory()
    )
    val uiState by albumViewModel.uiState.collectAsStateWithLifecycle()
    AlbumScreen(
        uiState = uiState,
        topBarActions = TopBarActions(onDrawerClicked = onDrawerClicked),
        bottomBarActions = ActionHandler.get().bottomBarActions,
        playerActions = ActionHandler.get().playerActions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    uiState: AlbumUiState,
    topBarActions: TopBarActions,
    bottomBarActions: BottomBarActions,
    playerActions: PlayerActions
) {
    ScreenWithTopBottomBar(
        uiState = uiState,
        title = stringResource(id = R.string.album_title),
        topBarActions = topBarActions,
        bottomBarActions = bottomBarActions,
        playerActions = playerActions
    ) {

        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1F)) {
            itemsIndexed(uiState.datas) { index, album ->
                val isFirstColumn = index % 2 == 0
                val startPaddingDp = when {
                    isFirstColumn -> 16.dp
                    else -> 8.dp
                }
                val endPaddingDp = when {
                    isFirstColumn -> 8.dp
                    else -> 16.dp
                }
                AlbumCard(
                    album = album,
                    onCardClicked = {},
                    startPaddingDp = startPaddingDp,
                    endPaddingDp = endPaddingDp
                )
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    onCardClicked: () -> Unit,
    startPaddingDp: Dp = 16.dp,
    endPaddingDp: Dp = 16.dp
) {
    Column(modifier = Modifier
        .padding(top = 8.dp, bottom = 8.dp, start = startPaddingDp, end = endPaddingDp)
        .clickable { }) {
        AlbumImage(
            data = album.getAlbumArtBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                .aspectRatio(1F)
                .clip(shape = MaterialTheme.shapes.large),
            placeHolderImage = R.drawable.default_album_art,
            errorImage = R.drawable.default_album_art
        )
        Column {
            var albumDesc = stringResource(
                id = R.string.formatter_album_count_desc,
                album.getSongCount()
            )
            album.getArtist()?.run {
                albumDesc += " $this"
            }
            Text(
                text = album.name,
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

@Preview
@Composable
private fun AlbumScreenPreview() {
    AlbumScreen(
        uiState = FakeDatas.albumUiState,
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
private fun AlbumCardPreview() {
    AlbumCard(FakeDatas.album, onCardClicked = {})
}