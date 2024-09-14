package com.hjkl.music.ui.bar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hjkl.comm.d
import com.hjkl.music.data.PlayerStateProvider

class PlayerViewModel : ViewModel() {

    val playerStateProvider = PlayerStateProvider.get()

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel() as T
            }
        }
    }

    init {
        "init".d()
        playerStateProvider.init()
    }

    override fun onCleared() {
        super.onCleared()
        "onCleared".d()
        playerStateProvider.destroy()
    }
}