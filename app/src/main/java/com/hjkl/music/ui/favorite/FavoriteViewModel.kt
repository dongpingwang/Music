package com.hjkl.music.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.db.DatabaseHelper
import com.hjkl.entity.Favorite
import com.hjkl.music.data.FavoriteManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val songs: List<Favorite> = emptyList(),
    val updateTimeMillis: Long? = null
)

class FavoriteViewModel : ViewModel() {

    val curSongFavoriteState = favoriteManager().curSongFavoriteState
    private val _favoriteSongState = MutableStateFlow(FavoriteUiState())
    val favoriteSongState = _favoriteSongState.asStateFlow()

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoriteViewModel() as T
            }
        }
    }

    fun favoriteManager() = FavoriteManager

    init {
        "init".d()
        viewModelScope.launch {
            DatabaseHelper.favoriteDao().getAllFlow().collect { songs ->
                _favoriteSongState.update {
                    it.copy(
                        songs = songs,
                        updateTimeMillis = System.currentTimeMillis()
                    )
                }
            }
        }
    }

}