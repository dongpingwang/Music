import SongViewModel.Companion.NULL_SUCCESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.comm.onBatchEach
import com.hjkl.comm.onFalse
import com.hjkl.comm.onTrue
import com.hjkl.entity.Song
import com.hjkl.music.data.GetAllSongsUseCase
import com.hjkl.music.data.PlayerStateProvider
import com.hjkl.player.constant.PlayMode
import com.hjkl.query.SongQuery
import com.hjkl.query.util.extraMetadataIfNeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class SongUiState {
    data class Error(val msg: String) : SongUiState()
    data class Success(
        val isLoading: Boolean,
        val songs: List<Song>,
        val curSong: Song?,
        val isPlaying: Boolean,
        val progressInMs: Long,
        val playMode: PlayMode,
        val playerErrorMsgOnce: String?,
        val updateTimeMillis: Long? // 数据获取或者解析完成时间戳
    ) : SongUiState()
}

fun SongUiState.asSuccess(): SongUiState.Success {
    return if (this is SongUiState.Success) {
        this
    } else {
        NULL_SUCCESS
    }
}

fun SongUiState.shortLog(): String {
    if (this is SongUiState.Success) {
        return "Success(isLoading=$isLoading, songs.size=${songs.size}, curSong=${curSong?.shortLog()}, isPlaying=$isPlaying, progressInMs=$progressInMs, playMode=$playMode, playerErrorMsg=$playerErrorMsgOnce, updateTimeMillis=$updateTimeMillis)"
    }
    return this.toString()
}

private data class SongViewModelState(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val songs: List<Song> = emptyList(),
    val curSong: Song? = null,
    val isPlaying: Boolean = false,
    val progressInMs: Long = 0L,
    val playMode: PlayMode = PlayMode.LIST,
    val playerErrorMsgOnce: String? = null,
    val updateTimeMillis: Long? = null
) {
    fun toUiState(): SongUiState {
        if (!errorMsg.isNullOrEmpty()) {
            return SongUiState.Error(errorMsg)
        }
        return SongUiState.Success(
            isLoading = isLoading,
            songs = songs,
            curSong = curSong,
            isPlaying = isPlaying,
            progressInMs = progressInMs,
            playMode = playMode,
            playerErrorMsgOnce = playerErrorMsgOnce,
            updateTimeMillis = updateTimeMillis
        )
    }
}

class SongViewModel : ViewModel() {
    companion object {
        val NULL_SUCCESS by lazy {
            SongUiState.Success(
                isLoading = false,
                songs = emptyList(),
                curSong = null,
                isPlaying = false,
                progressInMs = 0L,
                playMode = PlayMode.LIST,
                playerErrorMsgOnce = null,
                updateTimeMillis = System.currentTimeMillis()
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SongViewModel() as T
            }
        }
    }

    private val getAllSongsUseCase: GetAllSongsUseCase = GetAllSongsUseCase(SongQuery())
    private val viewModelState = MutableStateFlow(SongViewModelState())

    val uiState = viewModelState
        .map(SongViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())


    init {
        "init".d()
        fetchAllSongs(false)
        initPlayer()
    }

    override fun onCleared() {
        super.onCleared()
        "onCleared".d()
        player().destroy()
    }

    private fun initPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            "initPlayer".d()
            player().init()
            player().playerUiState.collect { playerState ->
                "playerUiState changed: $playerState".d()
                viewModelState.update {
                    it.copy(
                        curSong = playerState.curSong,
                        isPlaying = playerState.isPlaying,
                        progressInMs = playerState.progressInMs,
                        playMode = playerState.playMode,
                        playerErrorMsgOnce = playerState.playerErrorMsgOnce
                    )
                }
            }
        }
    }

    /**
     * 数据加载解析流程：
     * 1.从MediaContentProvider中获取歌曲列表，不包含专辑封面，完成后更新UI
     * 2.通过MediaMetadataRetriever解析专辑封面，完成后更新UI
     * 3.MediaMetadataRetriever解析比较耗时，eg：20ms/歌曲，每解析50个歌曲进行分批刷新
     */
    private fun fetchAllSongs(fromUser: Boolean) {
        "fetchAllSongs: fromUser=$fromUser".d()
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update { it.copy(isLoading = true) }
            "start fetch data from mediaprovider".d()
            getAllSongsUseCase.getAllSongs()
                .onSuccess { songs ->
                    "finish fetch data from mediaprovider".d()
                    fromUser.onFalse {
                        viewModelState.update {
                            it.copy(
                                songs = songs,
                                updateTimeMillis = System.currentTimeMillis()
                            )
                        }
                        "start extract data from mmr".d()
                        LogTrace.measureTimeMillis("SongViewModel#extraMetadataIfNeed()") {
                            songs.onBatchEach(10, 50) { index, item, isBatchFinish ->
                                item.extraMetadataIfNeed()
                                isBatchFinish.onTrue {
                                    viewModelState.update {
                                        it.copy(
                                            isLoading = (index + 1) != songs.size,
                                            songs = songs,
                                            updateTimeMillis = System.currentTimeMillis()
                                        )
                                    }
                                }
                            }
                        }
                        "finish extract data from mmr".d()
                    }.onTrue {
                        "start extract data from mmr".d()
                        LogTrace.measureTimeMillis("SongViewModel#extraMetadataIfNeed()") {
                            songs.onEach {
                                it.extraMetadataIfNeed()
                            }
                        }
                        viewModelState.update {
                            it.copy(
                                isLoading = false,
                                songs = songs,
                                updateTimeMillis = System.currentTimeMillis()
                            )
                        }
                        "finish extract data from mmr".d()
                    }
                }.onFailure { throwable ->
                    viewModelState.update { it.copy(errorMsg = throwable.message) }
                }

        }
    }

    fun refresh() {
        fetchAllSongs(true)
    }

    fun player() = PlayerStateProvider.get()
}
