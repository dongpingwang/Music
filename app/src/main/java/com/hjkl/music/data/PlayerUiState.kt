package com.hjkl.music.data

import com.hjkl.entity.Song
import com.hjkl.player.constant.RepeatMode

data class PlayerUiState(
    val curSong: Song?,
    val curPlayIndex: Int,
    val isPlaying: Boolean,
    val progressInMs: Long,
    val repeatMode: RepeatMode,
    val shuffled: Boolean,
    val playerErrorMsgOnce: String?,
    val toast: String?,
    val randomNoPlayContentDesc: String,
    val playlist: List<Song>
) {
    fun shortLog(): String {
        return "PlayerUiState(curSong=${curSong?.shortLog()}, curPlayIndex=${curPlayIndex}, isPlaying=${isPlaying}, progressInMs=$progressInMs, repeatMode=$repeatMode, shuffled=$shuffled, playerErrorMsgOnce=$playerErrorMsgOnce, playlist.size=${playlist.size})"
    }
}