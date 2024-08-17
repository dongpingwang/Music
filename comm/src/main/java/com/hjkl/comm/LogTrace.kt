package com.hjkl.comm

import android.util.Log

object LogTrace {

    private const val TAG = "LogTrace"

    fun <T> measureTimeMillis(
        tag: String,
        block: () -> T,
    ): T {
        val tname = Thread.currentThread().name
        val start = System.currentTimeMillis()
        val result = block()
        val cos = System.currentTimeMillis() - start
        Log.i(TAG, "$tag run at tname:$tname, cos:$cos(ms)")
        return result
    }
}