package com.hjkl.music.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.entity.Artist
import com.hjkl.music.ui.comm.CommViewModel
import com.hjkl.query.parseArtist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistViewModel : CommViewModel<Artist>() {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ArtistViewModel() as T
            }
        }
    }

    init {
        "init".d()
        initArtistSource()
    }

    private fun initArtistSource() {
        "initArtistSource".d()
        viewModelScope.launch(Dispatchers.IO) {
            source().songDataSourceState.collect { source ->
                "songDataSourceState changed: ${source.shortLog()}".d()
                viewModelState.update {
                    it.copy(
                        isFetchCompleted = source.isFetchCompleted,
                        isExtractCompleted = source.isFetchCompleted,
                        datas = source.songs.parseArtist(),
                        errorMsg = source.errorMsg,
                        updateTimeMillis = source.updateTimeMillis
                    )
                }
            }
        }
    }

}

