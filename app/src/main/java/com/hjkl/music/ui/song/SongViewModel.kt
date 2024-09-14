import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.comm.onFalse
import com.hjkl.comm.onTrue
import com.hjkl.entity.Song
import com.hjkl.music.data.AppConfig
import com.hjkl.music.data.Defaults
import com.hjkl.music.data.PlayerStateProvider
import com.hjkl.music.ui.comm.CommViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _playerUiState = MutableStateFlow(Defaults.defaultPlayerUiState)
    val playerUiState = _playerUiState.asStateFlow()

    init {
        viewModelScope.launch {
            "registerDataSourceStateObserver".d()
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
        AppConfig.isAppLaunched.onTrue {
            "启动初始化过，开始获取数据".d()
            fetchSongSource()
        }.onFalse {
            "还没启动初始化过，不需要获取数据".d()
            viewModelState.update { it.copy(isLoading = false) }
        }

        viewModelScope.launch {
            PlayerStateProvider.get().playerUiState.collect{
                _playerUiState.tryEmit(it)
            }
        }
    }

    private fun fetchSongSource() {
        "initSongSource".d()
        source().fetchAllSongs(false)
    }

    fun refresh() {
        source().fetchAllSongs(true)
    }

    fun scanMusic() {
        AppConfig.isAppLaunched = true
        source().fetchAllSongs(false)
    }

}
