package com.hjkl.comm

import android.app.Activity
import android.app.Application
import android.os.Bundle


object ActivityEventLogger {

    private lateinit var sApplication: Application

    fun init(application: Application) {
        sApplication = application
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    fun release() {
        sApplication.unregisterActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    private val activityLifecycleCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            "onActivityCreated: $activity".d()
        }

        override fun onActivityStarted(activity: Activity) {
            "onActivityStarted: $activity".d()
        }

        override fun onActivityResumed(activity: Activity) {
            "onActivityResumed: $activity".d()
        }

        override fun onActivityPaused(activity: Activity) {
            "onActivityPaused: $activity".d()
        }

        override fun onActivityStopped(activity: Activity) {
            "onActivityStopped: $activity".d()
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            "onActivitySaveInstanceState: $activity".d()
        }

        override fun onActivityDestroyed(activity: Activity) {
            "onActivityDestroyed: $activity".d()
        }
    }
}