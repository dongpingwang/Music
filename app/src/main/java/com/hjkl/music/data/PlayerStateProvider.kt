package com.hjkl.music.data

import androidx.annotation.FloatRange
import com.hjkl.comm.ResUtil
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.player.constant.PlayErrorCode
import com.hjkl.player.constant.PlayMode
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
    val isPlaying: Boolean,
    val progressInMs: Long,
    val playMode: PlayMode,
    val playerErrorMsgOnce: String?
)

class PlayerStateProvider {
    companion object {
        private val provider: PlayerStateProvider by lazy { PlayerStateProvider() }

        fun get(): PlayerStateProvider {
            return provider
        }
    }

    private val defaultPlayerUiState = PlayerUiState(
        curSong = null,
        isPlaying = false,
        progressInMs = 0L,
        playMode = PlayMode.LIST,
        playerErrorMsgOnce = null
    )

    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("PlayerStateProvider"))
    private val _playerUiState = MutableStateFlow(defaultPlayerUiState)
    val playerUiState = _playerUiState.asStateFlow()
    private val player = PlayerProxy

    // 是否正在调节进度条
    private var isUserSeeking = false

    // 调节进度后，500ms内不更新播放状态，从UI层面避免播放按钮状态切换闪烁
    private var userSeekingMillis = 0L

    // 当前页面 0--首页 1--播放器页面
    private var curPage = 0

    private val playSongChangedListener = object : (Song?) -> Unit {
        override fun invoke(song: Song?) {
            _playerUiState.update { it.copy(curSong = song) }
        }
    }

    private val isPlayingChangedListener = object : (Boolean) -> Unit {
        override fun invoke(isPlaying: Boolean) {
            if ((System.currentTimeMillis() - userSeekingMillis).absoluteValue < 500) {
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

    private val playModeChangedListener = object : (PlayMode) -> Unit {
        override fun invoke(playMode: PlayMode) {
            _playerUiState.update { it.copy(playMode = playMode) }
            AppConfig.playMode = playMode.getValue()
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
            if (errorCode == PlayErrorCode.ERROR_FORMAT_UNSUPPORTED) {
                _playerUiState.update { it.copy(playerErrorMsgOnce = ResUtil.getString(id = R.string.toast_format_unsupport)) }
            } else {
                _playerUiState.update { it.copy(playerErrorMsgOnce = ResUtil.getString(id = R.string.toast_unknown)) }
            }
            // 使用后进行清空
            scope.launch {
                delay(50)
                _playerUiState.update { it.copy(playerErrorMsgOnce = null) }
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
        getLatestPlayerState()
    }

    fun destroy() {
        "destroy".d()
        player.unregisterPlaySongChangedListener(playSongChangedListener)
        player.unregisterIsPlayingChangedListener(isPlayingChangedListener)
        player.unregisterPlayModeChangedListener(playModeChangedListener)
        player.unregisterPlayerReadyListener(playerReadyListener)
        player.unregisterPlayerErrorListener(playErrorListener)
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
                playMode = player.getPlayMode()
            )
        }
    }

    fun playAll(songs: List<Song>) {
        player.playSong(songs)
    }

    fun playIndex(songs: List<Song>, startIndex: Int) {
        player.playSong(songs, startIndex)
    }

    fun togglePlay() {
        _playerUiState.value.curSong?.let {
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
        _playerUiState.value.curSong?.let {
            player.prev()
        } ?: kotlin.run {
            "CurrentSong is null".d()
        }
    }

    fun playNext() {
        _playerUiState.value.curSong?.let {
            player.next()
        } ?: kotlin.run {
            "CurrentSong is null".d()
        }
    }

    fun setCurPage(curPage: Int) {
        this.curPage = curPage
        // 进入播放器界面，才需要监听进度变化，优化性能
        when (curPage) {
            0 -> {
                player.unregisterProgressChangedListener(progressChangedListener)
            }

            1 -> {
                _playerUiState.update { it.copy(progressInMs = player.getPosition()) }
                player.registerProgressChangedListener(progressChangedListener)
            }
        }
    }

    fun userInputSeekBar(isUserSeeking: Boolean, progressRatio: Float) {
        this.isUserSeeking = isUserSeeking
        if (isUserSeeking) {
            val position =
                (_playerUiState.value.curSong?.duration?.times(progressRatio))?.toLong() ?: 0L
            _playerUiState.update { it.copy(progressInMs = position) }
        } else {
            userSeekingMillis = System.currentTimeMillis()
            seekTo(progressRatio)
        }
    }

    fun seekTo(@FloatRange(from = 0.0, to = 1.0) progressRatio: Float) {
        _playerUiState.value.curSong?.let {
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