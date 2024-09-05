package com.hjkl.music.ui.comm

import com.hjkl.entity.Song
import com.hjkl.player.constant.PlayMode

data class ViewModelState<T>(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val datas: List<T> = emptyList(),
    val curSong: Song? = null,
    val isPlaying: Boolean = false,
    val progressInMs: Long = 0L,
    val playMode: PlayMode = PlayMode.LIST,
    val playerErrorMsgOnce: String? = null,
    val updateTimeMillis: Long? = null
)

typealias SongUiState = ViewModelState<Song>

fun <T> ViewModelState<T>.shortLog(): String {
    return "ViewModelState(isLoading=$isLoading, errorMsg=$errorMsg, datas.size=${datas.size}, curSong=${curSong?.shortLog()}, isPlaying=$isPlaying, progressInMs=$progressInMs, playMode=$playMode, playerErrorMsg=$playerErrorMsgOnce, updateTimeMillis=$updateTimeMillis)"
}

