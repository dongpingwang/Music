package com.hjkl.music.ui.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.CommonSongItem
import com.hjkl.music.ui.song.HeaderSongItem
import com.hjkl.music.utils.toSong
import com.hjkl.music.utils.toSongsFromFavorite


@Composable
fun FavoriteScreens(onDrawerClicked: () -> Unit) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModel.provideFactory()
    )
    val uiState by favoriteViewModel.favoriteSongState.collectAsStateWithLifecycle()
    val itemActions = ActionHandler.itemActions
    FavoriteScreens(
        uiState,
        onDrawerClicked = onDrawerClicked,
        onPlayAll = { itemActions.onPlayAll(uiState.songs.toSongsFromFavorite()) },
        onItemClicked = { itemActions.onItemClicked(uiState.songs.toSongsFromFavorite(), it) },
        onAddToQueue = itemActions.onAddToQueue
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreens(
    uiState: FavoriteUiState,
    onDrawerClicked: () -> Unit,
    onPlayAll: () -> Unit,
    onItemClicked: (Int) -> Unit,
    onAddToQueue: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(id = R.string.favorite_title))
        }, navigationIcon = {
            IconButton(onClick = onDrawerClicked) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        })
        if (uiState.songs.isNotEmpty()) {
            HeaderSongItem(count = uiState.songs.size,
                onPlayAll = onPlayAll,
                onEdit = {})
        }
        LazyColumn {
            itemsIndexed(uiState.songs) { index, favorite ->
                CommonSongItem(
                    song = favorite.toSong(),
                    onItemClicked = { onItemClicked(index) },
                    onAddToQueue = { onAddToQueue(favorite.toSong()) })
            }
        }
    }
}

@Preview
@Composable
private fun FavoriteScreensPreview(
) {
    FavoriteScreens(
        uiState = FakeDatas.favoriteUiState,
        onDrawerClicked = {},
        onPlayAll = {},
        onItemClicked = {},
        onAddToQueue = {})
}