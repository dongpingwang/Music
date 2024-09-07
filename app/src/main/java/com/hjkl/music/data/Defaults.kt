package com.hjkl.music.data

import com.hjkl.player.constant.PlayMode


object Defaults {

    val defaultPlayerUiState = PlayerUiState(
        curSong = null,
        isPlaying = false,
        progressInMs = 0L,
        playMode = PlayMode.LIST,
        playerErrorMsgOnce = null,
        toast = null,
        playlist = emptyList()
    )

    val defaultSongDataSourceState = SongDataSourceState(
        isLoading = true,
        songs = emptyList(),
        errorMsg = null,
        updateTimeMillis = null
    )
}