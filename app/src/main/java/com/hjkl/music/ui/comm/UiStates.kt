package com.hjkl.music.ui.comm

import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder
import com.hjkl.entity.Song

data class ViewModelState<T>(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val datas: List<T> = emptyList(),
    val updateTimeMillis: Long? = null
)

typealias SongUiState = ViewModelState<Song>
typealias AlbumUiState = ViewModelState<Album>
typealias ArtistUiState = ViewModelState<Artist>
typealias FolderUiState = ViewModelState<Folder>

fun <T> ViewModelState<T>.shortLog(): String {
    return "ViewModelState(isLoading=$isLoading, errorMsg=$errorMsg, datas.size=${datas.size}, updateTimeMillis=$updateTimeMillis)"
}

