package com.hjkl.music.ui.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hjkl.entity.Folder
import com.hjkl.music.R
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.bar.DrawerSearchTopAppBar
import com.hjkl.music.ui.comm.ActionHandler
import com.hjkl.music.ui.comm.FolderUiState

@Composable
fun FolderScreen(
    onDrawerClicked: () -> Unit
) {
    val folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModel.provideFactory()
    )
    val uiState by folderViewModel.uiState.collectAsStateWithLifecycle()
    val actionHandler = ActionHandler
    FolderScreen(
        uiState = uiState,
        onDrawerClicked = onDrawerClicked,
        onItemClicked= actionHandler.navigationActions.navigateToFolderDetail
    )
}

@Composable
fun FolderScreen(
    uiState: FolderUiState,
    onDrawerClicked: () -> Unit,
    onItemClicked: (Folder) -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        DrawerSearchTopAppBar(
            title = stringResource(id = R.string.folder_title),
            onDrawerClicked = onDrawerClicked,
            onSearchClicked = {}
        )
        LazyColumn(modifier = Modifier.weight(1F)) {
            itemsIndexed(uiState.datas) { index, folder ->
                FolderItem(folder = folder, onItemClicked = {onItemClicked(folder)})
            }
        }
    }
}

@Composable
private fun FolderItem(
    folder: Folder,
    onItemClicked: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClicked() }) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Image(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                var albumDesc = stringResource(
                    id = R.string.formatter_album_count_desc,
                    folder.getSongCount()
                )
                //  albumDesc += " ${folder.path}"
                Text(
                    text = folder.name,
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
    FolderScreen(
        uiState = FakeDatas.folderUiState,
        onDrawerClicked = {},
        onItemClicked = {}
    )
}

@Preview
@Composable
private fun AlbumItemPreview() {
    FolderItem(folder = FakeDatas.folder, onItemClicked = { })
}