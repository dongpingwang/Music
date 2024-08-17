import SongViewModel.Companion.PAGE_HOME
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.hjkl.entity.Song
import com.hjkl.music.data.GetAllSongsUseCase
import com.hjkl.player.PlayerProxy
import com.hjkl.query.SongQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SongUiState {
    data object Loading : SongUiState()
    data class Error(val msg: String) : SongUiState()
    data class Success(
        val songs: List<Song>,
        val curPage: Int,
        val curSong: Song?,
        val isPlaying: Boolean,
    ) : SongUiState()
}

private data class SongViewModelState(
    val loading: Boolean = true,
    val errorMsg: String? = null,
    val songs: List<Song> = emptyList(),
    val curPage: Int = PAGE_HOME,
    val curSong: Song? = null,
    val isPlaying: Boolean = false,
) {
    fun toUiState(): SongUiState {
        if (loading) {
            return SongUiState.Loading
        }
        if (!errorMsg.isNullOrEmpty()) {
            return SongUiState.Error(errorMsg)
        }
        return SongUiState.Success(
            songs = songs,
            curPage = curPage,
            curSong = curSong,
            isPlaying = isPlaying,
        )
    }
}

class SongViewModel : ViewModel() {
    companion object {
        private const val TAG = "SongViewModel"

        const val PAGE_HOME = 0 // 首页
        const val PAGE_PLAYER = 1 // 播放器页面
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val getAllSongsUseCase: GetAllSongsUseCase =
        GetAllSongsUseCase(ioDispatcher, SongQuery())
    private val player by lazy {
        PlayerProxy.player().apply {
            registerPlaySongChangedListener(playSongChangedListener)
            registerIsPlayingChangedListener(isPlayingChangedListener)
        }
    }

    private val viewModelState = MutableStateFlow(SongViewModelState())

    val uiState = viewModelState
        .map(SongViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())


    init {
        Log.d(TAG, "init")
        refresh()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
        player.unregisterPlaySongChangedListener(playSongChangedListener)
        player.unregisterIsPlayingChangedListener(isPlayingChangedListener)
    }

    fun refresh() {
        Log.d(TAG, "refresh")
        viewModelScope.launch {
            viewModelState.update { it.copy(loading = true) }
            getAllSongsUseCase.getAllSongs()
                .onSuccess { songs ->
                    viewModelState.update { it.copy(loading = false, songs = songs) }
                }.onFailure { throwable ->
                    viewModelState.update { it.copy(errorMsg = throwable.message) }
                }
        }
    }

    fun playAll() {
        player.playSong(viewModelState.value.songs)
    }

    fun playIndex(startIndex: Int) {
        player.playSong(viewModelState.value.songs, startIndex)
    }

    fun togglePlay() {
        player.getCurrentSong()?.let {
            if (player.isPlaying()) {
                player.pause()
            } else {
                player.play()
            }
        } ?: kotlin.run {
            Log.d(TAG, "CurrentSong is null")
        }
    }

    private val playSongChangedListener by lazy {
        object : (Song?) -> Unit {
            override fun invoke(song: Song?) {
                viewModelState.update { it.copy(curSong = song) }
            }
        }
    }

    private val isPlayingChangedListener by lazy {
        object : (Boolean) -> Unit {
            override fun invoke(isPlaying: Boolean) {
                viewModelState.update { it.copy(isPlaying = isPlaying) }
            }
        }
    }

}