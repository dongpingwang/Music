package com.hjkl.music.initializer

import android.app.Application
import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.db.DatabaseHelper
import com.hjkl.music.data.AppConfig
import com.hjkl.music.data.LyricStatePublisher
import com.hjkl.music.data.PlayerManager
import com.hjkl.music.utils.toSongs
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.media3.PlayerProxy
import com.hjkl.player.util.toRepeatMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Initializers {

    fun init(application: Application) {
        MainScope().launch(Dispatchers.IO) {
            LogTrace.measureTimeMillis("Initializers#initOnIoThread()") {
                DatabaseHelper.init(application)
                AppConfig.init()
                PlayerProxy.init(application, object : PlayerProxy.PlayerReadyListener {
                    override fun onPlayerReady(player: IPlayer) {
                        "onPlayerReady".d()
                        restorePlayerState(player)
                    }
                })
                PlayerManager.get().init()
                LyricStatePublisher.init()
            }
        }
    }

    fun destroy() {
        PlayerManager.get().destroy()
        LyricStatePublisher.destroy()
    }

    private fun restorePlayerState(player: IPlayer) {
        "restorePlayerState".d()
        val playMode = AppConfig.repeatMode.toRepeatMode()
        "恢复上次的重复播放模式: $playMode".d()
        player.setRepeatMode(playMode)
        val shuffled = AppConfig.shuffleMode
        "恢复上次的重复播放模式: $shuffled".d()
        player.setShuffleEnable(shuffled)

        MainScope().launch(Dispatchers.IO) {
            val lastPlayedIndex = AppConfig.lastPlayedIndex
            val lastPlayedPosition = AppConfig.lastPlayedPosition
            val playlist = DatabaseHelper.playlistDao().getAll().toSongs()
            "恢复上次的播放列表: playlistSize:${playlist.size} playedIndex=$lastPlayedIndex playedPosition=$lastPlayedPosition".d()
            launch(Dispatchers.Main) {
                player.playSong(
                    songs = playlist,
                    startIndex = lastPlayedIndex,
                    startPositionMs = lastPlayedPosition,
                    playWhenReady = false
                )
            }
        }
    }
}