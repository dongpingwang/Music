package com.hjkl.player.media3


import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProgressTracker {

    companion object {
        private const val TAG = "ProgressTracker"
    }

    private val progressChangedListeners = mutableListOf<(Long) -> Unit>()
    private var player: Player? = null
    private var currentMediaItem: MediaItem? = null
    private var isPlaying = false
    private var progressJob: Job? = null
    private var coroutine = CoroutineScope(CoroutineName(TAG))

    fun registerProgressChangedListener(listener: (Long) -> Unit): Boolean {
        return progressChangedListeners.contains(listener) || progressChangedListeners.add(listener)
    }

    fun unregisterProgressChangedListener(listener: (Long) -> Unit): Boolean {
        return progressChangedListeners.remove(listener)
    }

    fun setPlayer(player: Player) {
        this.player = player
    }

    fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d(TAG, "onIsPlayingChanged: $isPlaying")
        this.isPlaying = isPlaying
        if (isPlaying) {
            progressJob = coroutine.launch(Dispatchers.Main) {
                repeat(Int.MAX_VALUE) {
                    val position = player?.contentPosition ?: 0
                     // Log.d(TAG, "position: $position")
                    if (player?.currentMediaItem?.mediaId != currentMediaItem?.mediaId) {
                        // just ignore
                        Log.e(TAG, "player?.currentMediaItem is not currentMediaItem")
                    } else {
                        progressChangedListeners.onEach {
                            it(position)
                        }
                    }
                    delay(1000)
                }
            }
        } else {
            progressJob?.cancel()
            progressJob = null
        }
    }

    fun onPlaySongChanged(mediaItem: MediaItem?) {
        Log.d(TAG, "onPlaySongChanged: mediaId=${mediaItem?.mediaId}")
        currentMediaItem = mediaItem
        progressJob?.cancel()
        progressJob = null
        onIsPlayingChanged(isPlaying)
    }
}