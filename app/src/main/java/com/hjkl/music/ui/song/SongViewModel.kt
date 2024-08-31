import SongViewModel.Companion.NULL_SUCCESS
import androidx.annotation.FloatRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.comm.onBatchEach
import com.hjkl.entity.Song
import com.hjkl.music.data.AppConfig
import com.hjkl.music.data.GetAllSongsUseCase
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.media3.PlayerProxy
import com.hjkl.player.util.getValue
import com.hjkl.query.SongQuery
import com.hjkl.query.util.AlbumArtUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
        private val updateTimeMillis: Long?
    ) : SongUiState()
}

fun SongUiState.asSuccess(): SongUiState.Success {
    return if (this is SongUiState.Success) {
        this
    } else {
        NULL_SUCCESS
    }
}

private data class SongViewModelState(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val songs: List<Song> = emptyList(),
    val curSong: Song? = null,
    val isPlaying: Boolean = false,
    val progressInMs: Long = 0L,
    val playMode: PlayMode = PlayMode.LIST,
    private val updateTimeMillis: Long? = null
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
                updateTimeMillis = System.currentTimeMillis()
            )
        }
    }

    private val getAllSongsUseCase: GetAllSongsUseCase = GetAllSongsUseCase(SongQuery())
    private val viewModelState = MutableStateFlow(SongViewModelState())
    private val player:IPlayer = PlayerProxy

    // 是否正在调节进度条
    private var isUserSeeking = false

    val uiState = viewModelState
        .map(SongViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())


    private val playSongChangedListener = object : (Song?) -> Unit {
        override fun invoke(song: Song?) {
            viewModelState.update { it.copy(curSong = song) }
        }
    }

    private val isPlayingChangedListener = object : (Boolean) -> Unit {
        override fun invoke(isPlaying: Boolean) {
            viewModelState.update { it.copy(isPlaying = isPlaying) }
        }
    }

    private val progressChangedListener = object : (Long) -> Unit {
        override fun invoke(postion: Long) {
            if (!isUserSeeking) {
                viewModelState.update { it.copy(progressInMs = postion) }
            }
        }
    }

    private val playModeChangedListener = object : (PlayMode) -> Unit {
        override fun invoke(playMode: PlayMode) {
            viewModelState.update { it.copy(playMode = playMode) }
            AppConfig.playMode = playMode.getValue()
        }
    }

    init {
        "init".d()
        fetchAllSongs(false)
        player.registerPlaySongChangedListener(playSongChangedListener)
        player.registerIsPlayingChangedListener(isPlayingChangedListener)
        player.registerProgressChangedListener(progressChangedListener)
        player.registerPlayModeChangedListener(playModeChangedListener)

    }

    override fun onCleared() {
        super.onCleared()
        "onCleared".d()
        player.unregisterPlaySongChangedListener(playSongChangedListener)
        player.unregisterIsPlayingChangedListener(isPlayingChangedListener)
        player.unregisterProgressChangedListener(progressChangedListener)
        player.unregisterPlayModeChangedListener(playModeChangedListener)
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
            "start fetch data from database".d()
            getAllSongsUseCase.getAllSongs()
                .onSuccess { songs ->
                    "finish fetch data from database".d()
                    if (!fromUser) {
                        viewModelState.update {
                            it.copy(
                                songs = songs,
                                updateTimeMillis = System.currentTimeMillis()
                            )
                        }
                    }
                    "start extract data from mmr".d()
                    songs.onBatchEach(50) { index, item, isBatchFinish ->
                        if (item.bitmap == null) {
                            val albumArtBitmap = AlbumArtUtil.extractAlbumArtBitmap(item.data)
                            item.bitmap = albumArtBitmap?.second
                            item.originBitmapBytes = albumArtBitmap?.first
                        }
                        if (isBatchFinish) {
                            viewModelState.update {
                                it.copy(
                                    isLoading = (index + 1) != songs.size,
                                    songs = songs,
                                    updateTimeMillis = System.currentTimeMillis()
                                )
                            }
                        }
                    }
                    "finish extract data from mmr".d()
                }.onFailure { throwable ->
                    viewModelState.update { it.copy(errorMsg = throwable.message) }
                }

        }
    }

    fun refresh() {
        fetchAllSongs(true)
    }

    fun playAll() {
        player.playSong(viewModelState.value.songs)
    }

    fun playIndex(startIndex: Int) {
        player.playSong(viewModelState.value.songs, startIndex)
    }

    fun togglePlay() {
        viewModelState.value.curSong?.let {
            if (player.isPlaying()) {
                player.pause()
            } else {
                player.play()
            }
        } ?: kotlin.run {
            "CurrentSong is null".d()
        }
    }

    fun switchMode(curPlayMode: PlayMode) {
        val willPlayMode = when (curPlayMode) {
            PlayMode.LIST -> PlayMode.REPEAT_ONE
            PlayMode.REPEAT_ONE -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.LIST
        }
        player.setPlayMode(willPlayMode)
    }

    fun playPrev() {
        viewModelState.value.curSong?.let {
            player.prev()
        } ?: kotlin.run {
            "CurrentSong is null".d()
        }
    }

    fun playNext() {
        viewModelState.value.curSong?.let {
            player.next()
        } ?: kotlin.run {
            "CurrentSong is null".d()
        }
    }

    fun userInputSeekBar(isUserSeeking: Boolean, progressRatio: Float) {
        this.isUserSeeking = isUserSeeking
        if (isUserSeeking) {
            val position =
                (viewModelState.value.curSong?.duration?.times(progressRatio))?.toLong() ?: 0L
            viewModelState.update { it.copy(progressInMs = position) }
        } else {
            seekTo(progressRatio)
        }
    }

    fun seekTo(@FloatRange(from = 0.0, to = 1.0) progressRatio: Float) {
        viewModelState.value.curSong?.let {
            val position = it.duration.times(progressRatio).toLong()
            player.seekTo(position)
            if (!player.isPlaying()) {
                player.play()
            }
        } ?: kotlin.run {
            "CurrentSong is null".d()
        }
    }
}
