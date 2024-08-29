package com.hjkl.player

import androidx.media3.common.util.UnstableApi
import com.hjkl.comm.LogTrace
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.service.MusicService


object PlayerProxy {

    private const val TAG = "PlayerProxy"

    @delegate:UnstableApi
    private val player by lazy { Media3Player() }

    fun init() = LogTrace.measureTimeMillis("PlayerProxy#init()") {
        MusicService.start()
    }

    fun player(): IPlayer {
        return player
    }
}