package com.hjkl.music.ui.comm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.music.data.PlayerStateProvider
import com.hjkl.music.data.SongDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class CommViewModel<T> : ViewModel() {

    internal val viewModelState = MutableStateFlow(ViewModelState<T>())

    val uiState = viewModelState.asStateFlow()

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
        "initPlayer".d()
        player().init()
        viewModelScope.launch {
            player().playerUiState.collect { playerState ->
                "playerUiState changed: ${playerState.shortLog()}".d()
                viewModelState.update {
                    it.copy(playerUiState = playerState)
                }
            }
        }
    }

    private fun releasePlayer() {
        "releasePlayer".d()
        player().destroy()
    }
}