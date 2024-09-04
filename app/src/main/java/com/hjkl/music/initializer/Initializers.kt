package com.hjkl.music.initializer

import android.content.Context
import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.music.data.AppConfig
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.media3.PlayerProxy
import com.hjkl.player.util.toPlayMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object Initializers {

    fun init(application: Context) {
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

    }

    private fun restorePlayerState(player: IPlayer) {
        "restorePlayerState".d()
        val playMode = AppConfig.playMode.toPlayMode()
        "恢复上次的播放模式: $playMode".d()
        player.setPlayMode(playMode)
    }
}