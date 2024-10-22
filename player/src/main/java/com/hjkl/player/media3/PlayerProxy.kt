package com.hjkl.player.media3

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.player.interfaces.IPlayer
import com.hjkl.player.service.PlaybackService

object PlayerProxy : Media3Player() {

    private var mediaController: MediaController? = null
    private var isReady = false
    private val playerReadyListeners = arrayListOf<PlayerReadyListener>()

    fun init(context: Context, listener: PlayerReadyListener) = LogTrace.measureTimeMillis("PlayerProxy#init()") {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            "获取到MediaController".d()
            setPlayer(mediaController as Player)
            "注入Media3Player".d()
            init()
            "初始化Media3Player".d()
            isReady = true
            listener.onPlayerReady(this)
            playerReadyListeners.onEach { it.onPlayerReady(this) }
        }, ContextCompat.getMainExecutor(context))
    }

    fun isReady(): Boolean {
        return isReady
    }

    fun registerPlayerReadyListener(listener: PlayerReadyListener):Boolean {
        "registerPlayerReadyListener: $listener".d()
        return playerReadyListeners.contains(listener) || playerReadyListeners.add(listener)
    }

    fun unregisterPlayerReadyListener(listener: PlayerReadyListener):Boolean {
        "unregisterPlayerReadyListener: $listener".d()
        return playerReadyListeners.remove(listener)
    }

    interface PlayerReadyListener {
        fun onPlayerReady(player: IPlayer)
    }
}

