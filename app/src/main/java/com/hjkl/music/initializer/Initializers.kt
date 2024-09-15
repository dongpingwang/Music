package com.hjkl.music.initializer

import android.content.Context
import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.music.data.AppConfig
import com.hjkl.music.data.PlayerStateProvider
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.media3.PlayerProxy
import com.hjkl.player.util.toRepeatMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object Initializers {

    fun init(application: Context) {
        MainScope().launch {
            LogTrace.measureTimeMillis("Initializers#initOnUiThread()") {
                PlayerStateProvider.get().init()
            }
        }

        MainScope().launch(Dispatchers.IO) {
            LogTrace.measureTimeMillis("Initializers#initOnIoThread()") {
                AppConfig.init()
                PlayerProxy.init(application, object : PlayerProxy.PlayerReadyListener {
                    override fun onPlayerReady(player: IPlayer) {
                        "onPlayerReady".d()
                        restorePlayerState(player)
                    }
                })
            }
        }
    }

    fun destroy() {
        PlayerStateProvider.get().destroy()
    }

    private fun restorePlayerState(player: IPlayer) {
        "restorePlayerState".d()
        val playMode = AppConfig.repeatMode.toRepeatMode()
        "恢复上次的重复播放模式: $playMode".d()
        player.setRepeatMode(playMode)
        val shuffled = AppConfig.shuffleMode
        "恢复上次的重复播放模式: $shuffled".d()
        player.setShuffleEnable(shuffled)
    }
}