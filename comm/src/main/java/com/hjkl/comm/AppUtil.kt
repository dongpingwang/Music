package com.hjkl.comm

import android.app.Application
import android.content.Context

object AppUtil {

    private lateinit var sApplication: Application

    fun init(application: Application) {
        sApplication = application
    }

    fun getApplication(): Application {
        return sApplication
    }

    fun getContext(): Context {
        return sApplication
    }

}