package com.hjkl.music.ui.comm

import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder
import com.hjkl.entity.Song

data class ViewModelState<T>(
    val isFetchCompleted: Boolean = false,
    val isExtractCompleted: Boolean = false,
    val datas: List<T> = emptyList(),
    val errorMsg: String? = null,
    val updateTimeMillis: Long? = null
)

typealias SongUiState = ViewModelState<Song>
typealias AlbumUiState = ViewModelState<Album>
typealias ArtistUiState = ViewModelState<Artist>
typealias FolderUiState = ViewModelState<Folder>

fun <T> ViewModelState<T>.shortLog(): String {
    return "ViewModelState(isFetchFinish=$isFetchCompleted, isExtractFinish=$isExtractCompleted, errorMsg=$errorMsg, datas.size=${datas.size}, updateTimeMillis=$updateTimeMillis)"
}

