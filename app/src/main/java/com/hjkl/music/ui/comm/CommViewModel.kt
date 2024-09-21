package com.hjkl.music.ui.comm

import androidx.lifecycle.ViewModel
import com.hjkl.music.data.PlayerManager
import com.hjkl.music.data.SongDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class CommViewModel<T> : ViewModel() {

    internal val viewModelState = MutableStateFlow(ViewModelState<T>())

    val uiState = viewModelState.asStateFlow()

    fun source() = SongDataSource

    val playerUiState = PlayerManager.playerUiState
}