package com.hjkl.music.data

import android.util.Log
import com.drake.serialize.serialize.annotation.SerializeConfig
import com.drake.serialize.serialize.serial
import com.hjkl.comm.AppUtil
import com.tencent.mmkv.MMKV

@SerializeConfig(mmapID = "app_config")
object AppConfig {

    private const val TAG = "AppConfig"

    fun init() {
        Log.d(TAG, "init")
        MMKV.initialize(AppUtil.getContext())
    }

    // 播放模式
    var playMode: Int by serial(default = 0, name = "KEY_PLAY_MODE")
}