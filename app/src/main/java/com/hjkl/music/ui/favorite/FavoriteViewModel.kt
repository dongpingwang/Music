package com.hjkl.music.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hjkl.music.data.FavoriteManager


class FavoriteViewModel : ViewModel() {

    val curSongFavoriteState = FavoriteManager.curSongFavoriteState

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoriteViewModel() as T
            }
        }
    }

    fun favoriteManager() = FavoriteManager

}