package com.hjkl.player

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import com.hjkl.comm.AppUtil
import com.hjkl.comm.LogTrace
import com.hjkl.comm.createIfNull
import com.hjkl.comm.getOrDefault
import com.hjkl.entity.Song
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.util.findSong
import com.hjkl.player.util.toMediaItem

@UnstableApi
class Media3Player : IPlayer, Listener {

    companion object {
        const val TAG = "Media3Player"
    }

    private var _player: ExoPlayer? = null
    private val playlistManager by lazy { PlaylistManager() }
    private val playSongChangedListeners = mutableListOf<(Song?) -> Unit>()
    private val isPlayingChangedListeners = mutableListOf<(Boolean) -> Unit>()
    private val playModeChangedListeners = mutableListOf<(PlayMode) -> Unit>()
    private val progressTracker = ProgressTracker()

    private var isPlaying: Boolean = false
    private var curSong: Song? = null
    private var playMode: PlayMode? = null

    override fun init() {

    }

    override fun destroy() {
        _player?.removeListener(this)
        _player?.release()
        _player = null
    }

    override fun playSong(songs: List<Song>) {
        playSong(songs, 0)
    }

    override fun playSong(songs: List<Song>, startIndex: Int) {
        playlistManager.setPlaylist(songs)
        player().setMediaItems(songs.toMediaItem(), startIndex, 0L)
        player().prepare()
    }

    override fun getPlaylist(): List<Song> {
        return playlistManager.getPlaylist()
    }

    override fun getCurrentSong(): Song? {
        return curSong
    }

    override fun play() {
        player().play()
    }

    override fun pause() {
        player().pause()
    }

    override fun stop() {
        player().stop()
    }

    @OptIn(UnstableApi::class)
    override fun next() {
        player().next()
    }

    @OptIn(UnstableApi::class)
    override fun prev() {
        player().contentPosition
        player().previous()
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    override fun seekTo(positionMs: Long) {
        player().seekTo(positionMs)
    }

    override fun getDuration(): Long {
        return player().duration
    }

    override fun getPosition(): Long {
        return player().contentPosition
    }

    override fun setPlayMode(playMode: PlayMode) {
        when (playMode) {
            PlayMode.LIST -> {
                player().shuffleModeEnabled = false
                player().repeatMode = REPEAT_MODE_ALL
            }

            PlayMode.REPEAT_ONE -> {
                player().shuffleModeEnabled = false
                player().repeatMode = REPEAT_MODE_ONE
            }

            PlayMode.SHUFFLE -> {
                player().shuffleModeEnabled = true
            }
        }
        this.playMode = playMode
        playModeChangedListeners.onEach {
            it(playMode)
        }
    }

    override fun getPlayMode(): PlayMode {
        return playMode.getOrDefault(PlayMode.LIST)
    }

    override fun registerPlaySongChangedListener(listener: (Song?) -> Unit): Boolean {
        return playSongChangedListeners.contains(listener) || playSongChangedListeners.add(listener)
    }

    override fun unregisterPlaySongChangedListener(listener: (Song?) -> Unit): Boolean {
        return playSongChangedListeners.remove(listener)
    }

    override fun registerIsPlayingChangedListener(listener: (Boolean) -> Unit): Boolean {
        return isPlayingChangedListeners.contains(listener) || isPlayingChangedListeners.add(
            listener
        )
    }

    override fun unregisterIsPlayingChangedListener(listener: (Boolean) -> Unit): Boolean {
        return isPlayingChangedListeners.remove(listener)
    }

    override fun registerProgressChangedListener(listener: (Long) -> Unit): Boolean {
        return progressTracker.registerProgressChangedListener(listener)
    }

    override fun unregisterProgressChangedListener(listener: (Long) -> Unit): Boolean {
        return progressTracker.unregisterProgressChangedListener(listener)
    }

    override fun registerPlayModeChangedListener(listener: (PlayMode) -> Unit): Boolean {
        return playModeChangedListeners.contains(listener) || playModeChangedListeners.add(listener)
    }

    override fun unregisterPlayModeChangedListener(listener: (PlayMode) -> Unit): Boolean {
        return playModeChangedListeners.remove(listener)
    }

    private fun initializePlayer(): ExoPlayer =
        LogTrace.measureTimeMillis("Media3Player#initializePlayer()") {
            val playerBuilder = ExoPlayer.Builder(AppUtil.getContext())
            playerBuilder.build().apply {
                addListener(this@Media3Player)
                addAnalyticsListener(EventLogger())
                setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
                playWhenReady = true
            }
        }

    private fun player(): ExoPlayer {
        return _player.createIfNull {
            initializePlayer().apply {
                _player = this
                progressTracker.setPlayer(this)
            }
        }
    }


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        curSong = playlistManager.getPlaylist().findSong(mediaItem)
        progressTracker.onPlaySongChanged(mediaItem)
        Log.d(
            TAG,
            "onMediaItemTransition: mediaItem.id=${mediaItem?.mediaId} reason=$reason song=${curSong?.shortLog()}"
        )

        playSongChangedListeners.onEach {
            it(curSong)
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.d(TAG, "onPlaybackStateChanged: playbackState=$playbackState")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d(TAG, "onIsPlayingChanged: isPlaying=$isPlaying")
        this.isPlaying = isPlaying
        progressTracker.onIsPlayingChanged(isPlaying)
        isPlayingChangedListeners.onEach { it(isPlaying) }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.d(TAG, "onPlayerError: error=$error")
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        // Log.d(TAG, "onEvents: events=$events")
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
        Log.d(TAG, "onRepeatModeChanged: repeatMode=$repeatMode")
        when (repeatMode) {
            REPEAT_MODE_ALL, REPEAT_MODE_ONE -> {
                val playMode = if (repeatMode == REPEAT_MODE_ALL) PlayMode.LIST else PlayMode.REPEAT_ONE
                this.playMode = playMode
                playModeChangedListeners.onEach {
                    it(playMode)
                }
            }
            else -> {}
        }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        Log.d(TAG, "onShuffleModeEnabledChanged: shuffleModeEnabled=$shuffleModeEnabled")
        if (shuffleModeEnabled) {
            val playMode = PlayMode.SHUFFLE
            this.playMode = playMode
            playModeChangedListeners.onEach {
                it(playMode)
            }
        }
    }
}

