package com.hjkl.music.data

import androidx.annotation.StringRes
import com.hjkl.music.R
import com.hjkl.player.constant.RepeatMode


object Defaults {

    val defaultPlayerUiState = PlayerUiState(
        curSong = null,
        curPlayIndex = 0,
        isPlaying = false,
        progressInMs = 0L,
        repeatMode = RepeatMode.REPEAT_MODE_OFF,
        shuffled = false,
        playerErrorMsgOnce = null,
        toast = null,
        randomNoPlayContentDesc = "",
        playlist = emptyList()
    )

    val defaultSongDataSourceState = SongDataSourceState(
        isLoading = true,
        songs = emptyList(),
        errorMsg = null,
        updateTimeMillis = null
    )


    @StringRes
    fun randomNoPlayDescRes(): Int {
        val descResList = listOf(
            R.string.no_play_desc1,
            R.string.no_play_desc2,
            R.string.no_play_desc3,
            R.string.no_play_desc4,
            R.string.no_play_desc5,
            R.string.no_play_desc6,
            R.string.no_play_desc7,
            R.string.no_play_desc8,
            R.string.no_play_desc9,
            R.string.no_play_desc10,
        )
        return descResList.random()
    }
}