package com.hjkl.music

import android.app.Application
import android.util.Log
import com.hjkl.comm.AppUtil
import com.hjkl.comm.LogTrace
import com.hjkl.music.data.AppConfig
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.media3.PlayerProxy
import com.hjkl.player.util.toPlayMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MusicApplication : Application() {
    companion object {
        private const val TAG = "MusicApplication"
    }

    override fun onCreate() {
        super.onCreate()
        LogTrace.measureTimeMillis("MusicApplication#onCreateOnMainThread()") {
            AppUtil.init(this)
        }

        LogTrace.measureTimeMillis("MusicApplication#onCreateOnIoThread()") {
            MainScope().launch(Dispatchers.IO) {
                AppConfig.init()
                PlayerProxy.init(this@MusicApplication, object : PlayerProxy.PlayerCallBack {
                    override fun onPlayerReady(player: IPlayer) {
                        Log.d(TAG, "onPlayerReady")
                        restorePlayerState(player)
                    }
                })
            }
        }
    }

    private fun restorePlayerState(player: IPlayer) {
        Log.d(TAG, "restorePlayerState")
        val playMode = AppConfig.playMode.toPlayMode()
        Log.d(TAG, "恢复上次的播放模式: $playMode")
        player.setPlayMode(playMode)
    }

}