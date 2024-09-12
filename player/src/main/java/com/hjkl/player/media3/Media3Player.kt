package com.hjkl.player.media3

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import com.hjkl.comm.d
import com.hjkl.comm.getOrDefault
import com.hjkl.entity.Song
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.constant.RepeatMode
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.util.findSong
import com.hjkl.player.util.toMediaItem

open class Media3Player : IPlayer {

    private val playlistManager by lazy { PlaylistManager() }
    private val playSongChangedListeners = mutableListOf<(Song?) -> Unit>()
    private val isPlayingChangedListeners = mutableListOf<(Boolean) -> Unit>()
    private val playModeChangedListeners = mutableListOf<(PlayMode) -> Unit>()
    private val newPlayModeChangedListeners = mutableListOf<(RepeatMode?, Boolean?) -> Unit>()
    private val playerErrorListeners = mutableListOf<(Int) -> Unit>()
    private val progressTracker = ProgressTracker()

    private var isPlaying: Boolean = false
    private var curSong: Song? = null
    private var playMode: PlayMode? = null

    private lateinit var player: Player

    fun setPlayer(player: Player) {
        this.player = player
    }

    override fun init() {
        "init".d()
        player.addListener(playerListener)
        player.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
        player.playWhenReady = true
        progressTracker.setPlayer(player)
    }

    override fun destroy() {
        "destroy".d()
        player.removeListener(playerListener)
        player.release()
    }

    override fun playSong(songs: List<Song>) {
        playSong(songs, 0, true)
    }

    override fun playSong(songs: List<Song>, startIndex: Int, playWhenReady: Boolean) {
        "playSong: songs.size=${songs.size} startIndex=$startIndex playWhenReady=$playWhenReady".d()
        playlistManager.setPlaylist(songs)
        player.setMediaItems(songs.toMediaItem(), startIndex, 0L)
        player.playWhenReady = playWhenReady
        player.prepare()
    }

    override fun getPlaylist(): List<Song> {
        return playlistManager.getPlaylist()
    }

    override fun getCurrentSong(): Song? {
        return curSong
    }

    override fun play() {
        "play".d()
        player.play()
    }

    override fun pause() {
        "pause".d()
        player.pause()
    }

    override fun stop() {
        "stop".d()
        player.stop()
    }

    override fun next() {
        "next".d()
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
            player.playWhenReady = true
        } else {
            "hasn't NextMediaItem, playSongs".d()
            val playlist = ArrayList<Song>().apply { addAll(getPlaylist()) }
            playSong(playlist)
        }
    }

    override fun prev() {
        "prev".d()
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
            player.playWhenReady = true

        } else {
            "hasn't PreviousMediaItem, playSongs".d()
            val playlist = ArrayList<Song>().apply { addAll(getPlaylist()) }
            if (playlist.isNotEmpty()) {
                playSong(playlist, playlist.size - 1, true)
            }
        }
    }

    override fun addToNextPlay(song: Song, startPlay: Boolean) {
        "addToNextPlay: song=${song.shortLog()} startPlay=$startPlay".d()
        val currentPlayIndex = getCurrentPlayIndex()
        "currentPlayIndex: $currentPlayIndex".d()
        player.addMediaItem(currentPlayIndex + 1, song.toMediaItem())
        playlistManager.addSong(currentPlayIndex + 1, song)
        if (currentPlayIndex < 0) {
            // 没有播放过，添加时进行播放
            val playlist = ArrayList<Song>().apply { addAll(getPlaylist()) }
            playSong(playlist)
        } else {
            if (startPlay) {
                val playlist = ArrayList<Song>().apply { addAll(getPlaylist()) }
                playSong(playlist, currentPlayIndex + 1, true)
            }
        }
    }

    override fun getCurrentPlayIndex(): Int {
        // exo中player.currentMediaItemIndex不准，没有播放过，默认返回0，不符合预期
        if (curSong == null) {
            return -1
        }
        return player.currentMediaItemIndex
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    override fun seekTo(positionMs: Long) {
        "seekTo: $positionMs".d()
        player.seekTo(positionMs)
        player.playWhenReady = true
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun getPosition(): Long {
        return player.contentPosition
    }

    override fun setPlayMode(playMode: PlayMode) {
        "setPlayMode: $playMode".d()
        when (playMode) {
            PlayMode.LIST -> {
                player.shuffleModeEnabled = false
                player.repeatMode = Player.REPEAT_MODE_ALL
            }

            PlayMode.REPEAT_ONE -> {
                player.shuffleModeEnabled = false
                player.repeatMode = Player.REPEAT_MODE_ONE
            }

            PlayMode.SHUFFLE -> {
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.shuffleModeEnabled = true
            }
        }
        this.playMode = playMode
        playModeChangedListeners.onEach { it(playMode) }
    }

    override fun getPlayMode(): PlayMode {
        return playMode.getOrDefault(PlayMode.LIST)
    }

    override fun setRepeatMode(repeatMode: RepeatMode) {
        "setRepeatMode:$repeatMode".d()
        val playerRepeatMode = when (repeatMode) {
            RepeatMode.REPEAT_MODE_OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ONE
        }
        player.repeatMode = playerRepeatMode
        newPlayModeChangedListeners.onEach { it(repeatMode, null) }
    }

    override fun getRepeatMode(): RepeatMode {
        return toMyRepeatMode(player.repeatMode)
    }

    private fun toMyRepeatMode(@Player.RepeatMode repeatMode: Int): RepeatMode {
        return when (repeatMode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.REPEAT_MODE_OFF
            Player.REPEAT_MODE_ALL -> RepeatMode.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_MODE_ONE
            else -> RepeatMode.REPEAT_MODE_OFF
        }
    }

    override fun setShuffleEnable(shuffled: Boolean) {
        player.shuffleModeEnabled = shuffled
        newPlayModeChangedListeners.onEach { it(null, shuffled) }
    }

    override fun isShuffleEnable(): Boolean {
        return player.shuffleModeEnabled
    }

    override fun clearPlaylist() {
        player.clearMediaItems()
        playlistManager.clearPlaylist()
    }

    override fun removeItem(index: Int) {
        player.removeMediaItem(index)
        playlistManager.removeItem(index)
    }

    override fun registerPlaySongChangedListener(listener: (Song?) -> Unit): Boolean {
        "registerPlaySongChangedListener: $listener".d()
        return playSongChangedListeners.contains(listener) || playSongChangedListeners.add(listener)
    }

    override fun unregisterPlaySongChangedListener(listener: (Song?) -> Unit): Boolean {
        "unregisterPlaySongChangedListener: $listener".d()
        return playSongChangedListeners.remove(listener)
    }

    override fun registerIsPlayingChangedListener(listener: (Boolean) -> Unit): Boolean {
        "registerIsPlayingChangedListener: $listener".d()
        return isPlayingChangedListeners.contains(listener) || isPlayingChangedListeners.add(
            listener
        )
    }

    override fun unregisterIsPlayingChangedListener(listener: (Boolean) -> Unit): Boolean {
        "unregisterIsPlayingChangedListener: $listener".d()
        return isPlayingChangedListeners.remove(listener)
    }

    override fun registerProgressChangedListener(listener: (Long) -> Unit): Boolean {
        "registerProgressChangedListener: $listener".d()
        return progressTracker.registerProgressChangedListener(listener)
    }

    override fun unregisterProgressChangedListener(listener: (Long) -> Unit): Boolean {
        "unregisterProgressChangedListener: $listener".d()
        return progressTracker.unregisterProgressChangedListener(listener)
    }

    override fun registerPlayModeChangedListener(listener: (PlayMode) -> Unit): Boolean {
        "registerPlayModeChangedListener: $listener".d()
        return playModeChangedListeners.contains(listener) || playModeChangedListeners.add(listener)
    }

    override fun unregisterPlayModeChangedListener(listener: (PlayMode) -> Unit): Boolean {
        "unregisterPlayModeChangedListener: $listener".d()
        return playModeChangedListeners.remove(listener)
    }

    override fun registerPlayModeChangedListener(listener: (RepeatMode?, Boolean?) -> Unit): Boolean {
        "registerPlayModeChangedListener: $listener".d()
        return newPlayModeChangedListeners.contains(listener) || newPlayModeChangedListeners.add(
            listener
        )
    }

    override fun unregisterPlayModeChangedListener(listener: (RepeatMode?, Boolean?) -> Unit): Boolean {
        "unregisterPlayModeChangedListener: $listener".d()
        return newPlayModeChangedListeners.remove(listener)
    }

    override fun registerPlayerErrorListener(listener: (errorCode: Int) -> Unit): Boolean {
        "registerPlayerErrorListener: $listener".d()
        return playerErrorListeners.contains(listener) || playerErrorListeners.add(listener)
    }

    override fun unregisterPlayerErrorListener(listener: (errorCode: Int) -> Unit): Boolean {
        "unregisterPlayerErrorListener: $listener".d()
        return playerErrorListeners.remove(listener)
    }

    override fun registerPlaylistChangedListener(listener: (List<Song>) -> Unit): Boolean {
        "registerPlaylistChangedListener: $listener".d()
        return playlistManager.registerPlaylistChangedListener(listener)
    }

    override fun unregisterPlaylistChangedListener(listener: (List<Song>) -> Unit): Boolean {
        "unregisterPlaylistChangedListener: $listener".d()
        return playlistManager.unregisterPlaylistChangedListener(listener)
    }

    private val playerListener = object : Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            curSong = playlistManager.getPlaylist().findSong(mediaItem)
            progressTracker.onPlaySongChanged(mediaItem)
            "onMediaItemTransition: mediaItem.id=${mediaItem?.mediaId} reason=$reason song=${curSong?.shortLog()}".d()

            playSongChangedListeners.onEach {
                it(curSong)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            "onPlaybackStateChanged: playbackState=$playbackState".d()
        }

        override fun onIsPlayingChanged(_isPlaying: Boolean) {
            super.onIsPlayingChanged(_isPlaying)
            "onIsPlayingChanged: isPlaying=$_isPlaying".d()
            isPlaying = _isPlaying
            progressTracker.onIsPlayingChanged(_isPlaying)
            isPlayingChangedListeners.onEach { it(_isPlaying) }
        }

        // TODO exoplayer默认不支持ape音频格式
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            "onPlayerError: error=${error.errorCode} -- ${error.errorCodeName}".d()
            playerErrorListeners.onEach { it(error.errorCode) }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            // Log.d(TAG, "onEvents: events=$events")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            "onRepeatModeChanged: repeatMode=$repeatMode".d()
            newPlayModeChangedListeners.onEach { it(toMyRepeatMode(repeatMode), null) }
            when (repeatMode) {
                Player.REPEAT_MODE_ALL, Player.REPEAT_MODE_ONE -> {
                    val _playMode =
                        if (repeatMode == Player.REPEAT_MODE_ALL) PlayMode.LIST else PlayMode.REPEAT_ONE
                    playMode = _playMode
                    playModeChangedListeners.onEach {
                        it(_playMode)
                    }
                }

                else -> {}
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            "onShuffleModeEnabledChanged: shuffleModeEnabled=$shuffleModeEnabled".d()
            newPlayModeChangedListeners.onEach { it(null, shuffleModeEnabled) }
            if (shuffleModeEnabled) {
                val _playMode = PlayMode.SHUFFLE
                playMode = _playMode
                playModeChangedListeners.onEach {
                    it(_playMode)
                }
            }
        }
    }
}

