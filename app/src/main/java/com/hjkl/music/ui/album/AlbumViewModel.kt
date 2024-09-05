package com.hjkl.music.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hjkl.music.ui.comm.CommViewModel

class AlbumViewModel() : CommViewModel<AlbumViewModel>() {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AlbumViewModel() as T
            }
        }
    }
}