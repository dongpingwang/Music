package com.hjkl.comm

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
        "$tag run at tname:$tname, cos:$cos(ms)".tag(TAG).d()
        return result
    }
}