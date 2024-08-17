package com.hjkl.music

import android.app.Application
import com.hjkl.comm.AppUtil
import com.hjkl.comm.LogTrace
import com.hjkl.player.PlayerProxy

class MusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LogTrace.measureTimeMillis("MusicApplication#onCreate()") {
            AppUtil.init(this)
            PlayerProxy.init()
        }
    }

}