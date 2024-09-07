import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.ui.comm.CommViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SongViewModel : CommViewModel<Song>() {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SongViewModel() as T
            }
        }
    }

    init {
        "init".d()
        initSongSource()
    }

    private fun initSongSource() {
        "initSongSource".d()
        viewModelScope.launch {
            source().fetchAllSongs(false)
            source().songDataSourceState.collect { source ->
                "songDataSourceState changed: ${source.shortLog()}".d()
                viewModelState.update {
                    it.copy(
                        isLoading = source.isLoading,
                        errorMsg = source.errorMsg,
                        datas = source.songs,
                        updateTimeMillis = source.updateTimeMillis
                    )
                }
            }
        }
    }

    fun refresh() {
        source().fetchAllSongs(true)
    }

}
