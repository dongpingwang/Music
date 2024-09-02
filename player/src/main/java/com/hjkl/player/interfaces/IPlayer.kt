package com.hjkl.player.interfaces

import com.hjkl.entity.Song
import com.hjkl.player.constant.PlayMode

interface IPlayer {

    /**
     * 初始化
     */
    fun init()

    /**
     * 释放
     */
    fun destroy()

    /**
     * 播放歌曲，从第一首开始播放
     */
    fun playSong(songs: List<Song>)

    /**
     * 播放歌曲，从指定的歌曲开始播放
     */
    fun playSong(songs: List<Song>, startIndex: Int)

    /**
     * 获取播放列表
     */
    fun getPlaylist(): List<Song>

    /**
     * 获取当前的歌曲
     */
    fun getCurrentSong(): Song?

    /**
     * 继续播放
     */
    fun play()

    /**
     * 暂停播放
     */
    fun pause()

    /**
     * 停止播放
     */
    fun stop()

    /**
     * 下一首
     */
    fun next()


    /**
     * 上一首
     */
    fun prev()

    /**
     * 判断是否在播放中
     */
    fun isPlaying(): Boolean

    /**
     * 设置进度
     */

    fun seekTo(positionMs: Long)

    /**
     * 获取歌曲时长
     */
    fun getDuration(): Long

    /**
     * 获取当前进度
     */
    fun getPosition(): Long

    /**
     * 设置播放模式
     */
    fun setPlayMode(playMode: PlayMode)

    /**
     * 获取播放模式
     */
    fun getPlayMode(): PlayMode

    /**
     * 当前歌曲变化监听
     */
    fun registerPlaySongChangedListener(listener: (Song?) -> Unit): Boolean

    fun unregisterPlaySongChangedListener(listener: (Song?) -> Unit): Boolean

    /**
     * 播放状态变化监听
     */
    fun registerIsPlayingChangedListener(listener: (Boolean) -> Unit): Boolean

    fun unregisterIsPlayingChangedListener(listener: (Boolean) -> Unit): Boolean

    /**
     * 播放进度变化监听
     */
    fun registerProgressChangedListener(listener: (Long) -> Unit): Boolean

    fun unregisterProgressChangedListener(listener: (Long) -> Unit): Boolean

    /**
     * 播放模式变化监听
     */
    fun registerPlayModeChangedListener(listener: (PlayMode) -> Unit): Boolean

    fun unregisterPlayModeChangedListener(listener: (PlayMode) -> Unit): Boolean

    /**
     * 播放失败事件监听
     */
    fun registerPlayerErrorListener(listener: (errorCode: Int) -> Unit): Boolean

    fun unregisterPlayerErrorListener(listener: (errorCode: Int) -> Unit): Boolean
}