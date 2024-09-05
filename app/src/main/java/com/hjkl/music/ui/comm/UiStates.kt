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

fun ViewModelState<Song>.toUiState(): SongUiState {
    if (!errorMsg.isNullOrEmpty()) {
        return SongUiState.Error(errorMsg)
    }
    return SongUiState.Success(
        isLoading = isLoading,
        songs = datas,
        curSong = curSong,
        isPlaying = isPlaying,
        progressInMs = progressInMs,
        playMode = playMode,
        playerErrorMsgOnce = playerErrorMsgOnce,
        updateTimeMillis = updateTimeMillis
    )
}


object Defaults {
    val songUiState = SongUiState.Success(
        isLoading = false,
        songs = emptyList(),
        curSong = null,
        isPlaying = false,
        progressInMs = 0L,
        playMode = PlayMode.LIST,
        playerErrorMsgOnce = null,
        updateTimeMillis = System.currentTimeMillis()
    )
}

sealed class SongUiState {
    data class Error(val msg: String) : SongUiState()
    data class Success(
        val isLoading: Boolean,
        val songs: List<Song>,
        val curSong: Song?,
        val isPlaying: Boolean,
        val progressInMs: Long,
        val playMode: PlayMode,
        val playerErrorMsgOnce: String?,
        val updateTimeMillis: Long?
    ) : SongUiState()
}

fun SongUiState.asSuccess(): SongUiState.Success {
    return if (this is SongUiState.Success) {
        this
    } else {
        Defaults.songUiState
    }
}

fun SongUiState.shortLog(): String {
    if (this is SongUiState.Success) {
        return "Success(isLoading=$isLoading, songs.size=${songs.size}, curSong=${curSong?.shortLog()}, isPlaying=$isPlaying, progressInMs=$progressInMs, playMode=$playMode, playerErrorMsg=$playerErrorMsgOnce, updateTimeMillis=$updateTimeMillis)"
    }
    return this.toString()
}

