package com.hjkl.music.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hjkl.music.data.LyricStatePublisher


class LyricViewModel : ViewModel() {
    val curLyricState = LyricStatePublisher.curLyricState

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LyricViewModel() as T
            }
        }
    }
}