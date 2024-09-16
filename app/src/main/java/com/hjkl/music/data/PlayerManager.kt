package com.hjkl.music.data


import com.hjkl.comm.ResUtil
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.data.Defaults.defaultPlayerUiState
import com.hjkl.player.constant.PlayErrorCode
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.constant.RepeatMode
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.media3.PlayerProxy
import com.hjkl.player.util.getValue
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class PlayerUiState(
    val curSong: Song?,
    val curPlayIndex: Int,
    val isPlaying: Boolean,
    val progressInMs: Long,
    val repeatMode: RepeatMode,
    val shuffled: Boolean,
    val playerErrorMsgOnce: String?,
    val toast: String?,
    val randomNoPlayContentDesc: String,
    val playlist: List<Song>
) {
    fun shortLog(): String {
        return "PlayerUiState(curSong=${curSong?.shortLog()}, curPlayIndex=${curPlayIndex}, isPlaying=${isPlaying}, progressInMs=$progressInMs, repeatMode=$repeatMode, shuffled=$shuffled, playerErrorMsgOnce=$playerErrorMsgOnce, playlist.size=${playlist.size})"
    }
}

class PlayerManager private constructor() {
    companion object {
        private val provider: PlayerManager by lazy { PlayerManager() }

        fun get(): PlayerManager {
            return provider
        }
    }

    private val scope = CoroutineScope(CoroutineName("PlayerStateProvider"))
    private val _playerUiState = MutableStateFlow(defaultPlayerUiState)
    val playerUiState = _playerUiState.asStateFlow()
    private val player = PlayerProxy

    // 是否正在调节进度条
    private var isUserSeeking = false

    // 播放中，调节进度后，500ms内不更新播放状态，从UI层面避免播放按钮状态切换闪烁
    private var userSeekingMillis = 0L
    private var playingWhenUserSeeking = false

    private val playSongChangedListener = object : (Song?) -> Unit {
        override fun invoke(song: Song?) {
            _playerUiState.update {
                it.copy(
                    curSong = song,
                    curPlayIndex = player.getCurrentPlayIndex()
                )
            }
            if (song == null) {
                _playerUiState.update { it.copy(isPlaying = false) }
            }
        }
    }

    private val isPlayingChangedListener = object : (Boolean) -> Unit {
        override fun invoke(isPlaying: Boolean) {
            if ((System.currentTimeMillis() - userSeekingMillis).absoluteValue < 500 && playingWhenUserSeeking) {
                "调节进度后，500ms内不更新播放状态，从UI层面避免播放按钮状态切换闪烁".d()
                return
            }
            _playerUiState.update { it.copy(isPlaying = isPlaying) }
        }
    }

    private val progressChangedListener = object : (Long) -> Unit {
        override fun invoke(postion: Long) {
            if (!isUserSeeking) {
                _playerUiState.update { it.copy(progressInMs = postion) }
            }
        }
    }

    private val playModeChangedListener = object : (RepeatMode?, Boolean?) -> Unit {
        override fun invoke(repeatMode: RepeatMode?, shuffled: Boolean?) {
            repeatMode?.run {
                _playerUiState.update { it.copy(repeatMode = this) }
                AppConfig.repeatMode = this.getValue()
            }
            shuffled?.run {
                _playerUiState.update { it.copy(shuffled = this) }
                AppConfig.shuffleMode = this
            }
        }
    }

    private val playerReadyListener = object : PlayerProxy.PlayerReadyListener {
        override fun onPlayerReady(player: IPlayer) {
            "播放器准备好了，获取最新的播放器状态".d()
            getLatestPlayerState()
        }
    }

    private val playErrorListener = object : (Int) -> Unit {
        override fun invoke(errorCode: Int) {
            val errorMsg = if (errorCode == PlayErrorCode.ERROR_FORMAT_UNSUPPORTED) {
                ResUtil.getString(id = R.string.toast_format_unsupport)
            } else {
                ResUtil.getString(id = R.string.toast_unknown)
            }
            updateToastOnce(errorMsg)
            _playerUiState.update { it.copy(isPlaying = false) }
        }
    }


    private val playlistChangedListener = object : (List<Song>) -> Unit {
        override fun invoke(playlist: List<Song>) {
            "playlistChanged: ${playlist.size}".d()
            _playerUiState.update { it.copy(playlist = playlist) }
            if (playlist.isEmpty()) {
                _playerUiState.update { it.copy(randomNoPlayContentDesc = ResUtil.getString(Defaults.randomNoPlayDescRes())) }
            }
        }
    }

    fun init() {
        "init".d()
        player.registerPlaySongChangedListener(playSongChangedListener)
        player.registerIsPlayingChangedListener(isPlayingChangedListener)
        player.registerPlayModeChangedListener(playModeChangedListener)
        player.registerPlayerReadyListener(playerReadyListener)
        player.registerPlayerErrorListener(playErrorListener)
        player.registerPlaylistChangedListener(playlistChangedListener)
        player.registerProgressChangedListener(progressChangedListener)
        getLatestPlayerState()
    }

    fun destroy() {
        "destroy".d()
        player.unregisterPlaySongChangedListener(playSongChangedListener)
        player.unregisterIsPlayingChangedListener(isPlayingChangedListener)
        player.unregisterPlayModeChangedListener(playModeChangedListener)
        player.unregisterPlayerReadyListener(playerReadyListener)
        player.unregisterPlayerErrorListener(playErrorListener)
        player.unregisterPlaylistChangedListener(playlistChangedListener)
        player.unregisterProgressChangedListener(progressChangedListener)
    }

    private fun getLatestPlayerState() {
        if (!player.isReady()) {
            "播放器还未准备好".d()
            return
        }
        val curSong = player.getCurrentSong()
        if (curSong == null) {
            "当前没有播放内容".d()
            return
        }
        "获取最新的播放器状态".d()
        _playerUiState.update {
            it.copy(
                curSong = curSong,
                isPlaying = player.isPlaying(),
                progressInMs = player.getPosition(),
                repeatMode = player.getRepeatMode(),
                shuffled = player.isShuffleEnable()
            )
        }
    }

    fun playAll(songs: List<Song>) {
        player.playSong(songs)
    }

    fun playIndex(songs: List<Song>, startIndex: Int, playWhenReady: Boolean) {
        player.playSong(songs, startIndex, playWhenReady)
    }

    fun maybePlayIndex(songs: List<Song>, startIndex: Int): Boolean {
        if (player.getCurrentSong()?.id == songs[startIndex].id) {
            // 播放是这首歌，进到播放器界面
            return true
        }
        // 播的不是这首歌，则进行播放
        playIndex(songs, startIndex, true)
        return false
    }

    fun togglePlay() {
        runIfHasPlayedContent {
            if (player.isPlaying()) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun maybeTogglePlay(songs: List<Song>, startIndex: Int) {
        if (player.getCurrentSong()?.id == songs[startIndex].id) {
            // 播放是这首歌，切换暂停或者播放
            togglePlay()
            return
        }
        // 设置播放列表，并播放
        playIndex(songs, startIndex, true)
    }

    @Deprecated("", replaceWith = ReplaceWith("switchRepeatMode()", ""))
    fun switchMode(curPlayMode: PlayMode) {
        val willPlayMode = when (curPlayMode) {
            PlayMode.LIST -> PlayMode.REPEAT_ONE
            PlayMode.REPEAT_ONE -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.LIST
        }
        player.setPlayMode(willPlayMode)
    }


    fun switchRepeatMode(curRepeatMode: RepeatMode) {
        val willPlayMode = when (curRepeatMode) {
            RepeatMode.REPEAT_MODE_OFF -> RepeatMode.REPEAT_MODE_ALL
            RepeatMode.REPEAT_MODE_ALL -> RepeatMode.REPEAT_MODE_ONE
            RepeatMode.REPEAT_MODE_ONE -> RepeatMode.REPEAT_MODE_OFF
        }
        player.setRepeatMode(willPlayMode)
    }

    fun setShuffleEnable(shuffled: Boolean) {
        player.setShuffleEnable(shuffled)
    }

    fun playPrev() {
        runIfHasPlayedContent { player.prev() }
    }

    fun playNext() {
        runIfHasPlayedContent { player.next() }
    }

    fun addToNextPlay(song: Song) {
        player.addToNextPlay(song, false)
        updateToastOnce(ResUtil.getString(R.string.toast_add_to_next_play_success))
    }

    fun clearPlaylist() {
        player.clearPlaylist()
    }

    fun removeItem(index: Int) {
        player.removeItem(index)
    }

    fun userInputSeekBar(isUserSeeking: Boolean, progressInMillis: Long) {
        this.isUserSeeking = isUserSeeking
        if (isUserSeeking) {
            // _playerUiState.update { it.copy(progressInMs = progressInMillis) }
        } else {
            userSeekingMillis = System.currentTimeMillis()
            playingWhenUserSeeking = player.isPlaying()
            seekTo(progressInMillis)
        }
    }

    private fun seekTo(progressInMs: Long) {
        runIfHasPlayedContent { player.seekTo(progressInMs) }
    }

    private fun runIfHasPlayedContent(block: () -> Unit) {
        _playerUiState.value.curSong?.let {
            block()
        } ?: kotlin.run {
            "no played content".d()
        }
    }

    private fun updateToastOnce(errorMsg: String? = null, toast: String? = null) {
        if (errorMsg != null) {
            _playerUiState.update { it.copy(playerErrorMsgOnce = errorMsg) }
            // 使用后进行清空
            scope.launch(Dispatchers.IO) {
                delay(50)
                _playerUiState.update { it.copy(playerErrorMsgOnce = null) }
            }
        }
        if (toast != null) {
            _playerUiState.update { it.copy(toast = toast) }
            // 使用后进行清空
            scope.launch(Dispatchers.IO) {
                delay(50)
                _playerUiState.update { it.copy(toast = null) }
            }
        }
    }
}