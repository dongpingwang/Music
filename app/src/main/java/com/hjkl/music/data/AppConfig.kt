package com.hjkl.music.data

import android.util.Log
import com.drake.serialize.serialize.annotation.SerializeConfig
import com.drake.serialize.serialize.serial
import com.hjkl.player.constant.RepeatMode

import com.hjkl.player.util.getValue

@SerializeConfig(mmapID = "app_config")
object AppConfig {

    private const val TAG = "AppConfig"

    fun init() {
        Log.d(TAG, "init")
    }

    // 重复播放模式
    var repeatMode: Int by serial(
        default = RepeatMode.REPEAT_MODE_OFF.getValue(),
        name = "KEY_REPEAT_MODE"
    )

    // 随机播放模式
    var shuffleMode: Boolean by serial(default = false, name = "KEY_SHUFFLE_MODE")

    // 是否app启动初始化过
    var isAppLaunched: Boolean by serial(default = false, name = "KEY_IS_APP_LAUNCHED")

    // 播放列表中的位置
    var lastPlayedIndex: Int by serial(
        default = 0,
        name = "KEY_LAST_PLAYED_INDEX"
    )
    // 播放进度
    var lastPlayedPosition: Long by serial(
        default = 0L,
        name = "KEY_LAST_PLAYED_POSITION"
    )
}