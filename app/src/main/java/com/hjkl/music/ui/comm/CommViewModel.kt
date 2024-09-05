package com.hjkl.music.ui.comm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.music.data.PlayerStateProvider
import com.hjkl.music.data.SongDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


abstract class CommViewModel<T> : ViewModel() {

    internal val viewModelState = MutableStateFlow(ViewModelState<T>())


    fun player() = PlayerStateProvider.get()

    fun source() = SongDataSource.get()

    init {
        "init".d()
        initPlayer()
    }

    override fun onCleared() {
        super.onCleared()
        "onCleared".d()
        releasePlayer()
    }

    private fun initPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            "initPlayer".d()
            player().init()
            player().playerUiState.collect { playerState ->
                "playerUiState changed: $playerState".d()
                viewModelState.update {
                    it.copy(
                        curSong = playerState.curSong,
                        isPlaying = playerState.isPlaying,
                        progressInMs = playerState.progressInMs,
                        playMode = playerState.playMode,
                        playerErrorMsgOnce = playerState.playerErrorMsgOnce
                    )
                }
            }
        }
    }

    private fun releasePlayer() {
        "releasePlayer".d()
        player().destroy()
    }
}