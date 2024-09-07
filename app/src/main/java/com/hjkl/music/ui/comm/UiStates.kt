package com.hjkl.music.ui.comm

import com.hjkl.entity.Album
import com.hjkl.entity.Song
import com.hjkl.music.data.Defaults.defaultPlayerUiState
import com.hjkl.music.data.PlayerUiState

data class ViewModelState<T>(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val datas: List<T> = emptyList(),
    val playerUiState: PlayerUiState = defaultPlayerUiState,
    val updateTimeMillis: Long? = null
)

typealias SongUiState = ViewModelState<Song>
typealias AlbumUiState = ViewModelState<Album>

fun <T> ViewModelState<T>.shortLog(): String {
    return "ViewModelState(isLoading=$isLoading, errorMsg=$errorMsg, datas.size=${datas.size}, playerUiState=${playerUiState.shortLog()}, updateTimeMillis=$updateTimeMillis)"
}

