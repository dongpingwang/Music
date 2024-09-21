package com.hjkl.music.ui.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Folder
import com.hjkl.entity.Song
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.CommonSongItem
import com.hjkl.music.ui.song.HeaderSongItem


@Composable
fun FolderDetailScreen(folderPath: String) {
    val folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModel.provideFactory()
    )
    val uiState by folderViewModel.uiState.collectAsStateWithLifecycle()

    val actionHandler = ActionHandler

    val curFolder = uiState.datas.find { it.path == folderPath }

    curFolder?.let { folder ->
        FolderDetailScreen(
            folder = folder,
            onBackClicked = actionHandler.navigationActions.popBackStack,
            onPlayAll = { actionHandler.itemActions.onPlayAll(folder.getSongs()) },
            onItemClicked = { actionHandler.itemActions.onItemClicked(folder.getSongs(), it) },
            onAddToQueue = actionHandler.itemActions.onAddToQueue
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folder: Folder,
    onBackClicked: () -> Unit,
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
            Text(text = folder.name)
        }, navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
            }
        })
        HeaderSongItem(count = folder.getSongCount(),
            onPlayAll = onPlayAll,
            onEdit = {})
        LazyColumn {
            itemsIndexed(folder.getSongs()) { index, song ->
                CommonSongItem(
                    song = song,
                    onItemClicked = { onItemClicked(index) },
                    onAddToQueue = { onAddToQueue(song) })
            }
        }
    }
}

@Preview
@Composable
private fun FolderDetailScreenPreview() {
    FolderDetailScreen(
        folder = FakeDatas.folder,
        onBackClicked = {},
        onPlayAll = {},
        onItemClicked = {},
        onAddToQueue = {})
}