package com.hjkl.comm

import android.app.Application


abstract class BaseApplication() : Application() {

    abstract fun init()

    abstract fun destroy()

    override fun onCreate() {
        super.onCreate()
        "onCreate".d()
        AppUtil.init(this)
        ActivityEventLogger.init(this)
        init()
    }

    override fun onTerminate() {
        super.onTerminate()
        "onTerminate".d()
        ActivityEventLogger.release()
        destroy()
    }
}