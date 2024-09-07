package com.hjkl.music.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.entity.Album
import com.hjkl.music.ui.comm.CommViewModel
import com.hjkl.query.parseAlbum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumViewModel : CommViewModel<Album>() {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AlbumViewModel() as T
            }
        }
    }

    init {
        "init".d()
        initAlbumSource()
    }

    private fun initAlbumSource() {
        "initAlbumSource".d()
        viewModelScope.launch(Dispatchers.IO) {
            source().songDataSourceState.collect { source ->
                "songDataSourceState changed: ${source.shortLog()}".d()
                source.songs.parseAlbum()
                viewModelState.update {
                    it.copy(
                        isLoading = source.isLoading,
                        errorMsg = source.errorMsg,
                        datas = source.songs.parseAlbum(),
                        updateTimeMillis = source.updateTimeMillis
                    )
                }
            }
        }
    }

}

