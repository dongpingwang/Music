package com.hjkl.comm

import android.os.Build
import android.util.Log
import java.util.regex.Pattern

/**
 * Log工具类
 * 用法 :
 *     "log".d()
 *     "log".tag("tag").d()
 *     "log".tag("tag").throwable(ex).d()
 */
private const val sTag = "LogKTX"
private val sTagMap = ThreadLocal<String?>()
private const val MAX_LOG_LENGTH = 4000
private const val MAX_TAG_LENGTH = 23
private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

fun String.tag(tag: String): String {
    sTagMap.set(tag)
    return this
}

fun String.throwable(tr: Throwable): Pair<String, Throwable> {
    return Pair(this, tr)
}

fun String.d() {
    prepareLog(Log.DEBUG,this,null)
}

fun String.e() {
    prepareLog(Log.ERROR,this,null)
}

fun String.i() {
    prepareLog(Log.INFO,this,null)
}

fun String.v() {
    prepareLog(Log.VERBOSE,this,null)
}

fun String.wtf() {
    prepareLog(Log.ASSERT,this,null)
}

fun String.w() {
    prepareLog(Log.WARN,this,null)
}

fun Pair<String, Throwable>.d() {
    prepareLog(Log.DEBUG,this.first,this.second)
}

fun Pair<String, Throwable>.e() {
    prepareLog(Log.ERROR,this.first,this.second)
}

fun Pair<String, Throwable>.v() {
    prepareLog(Log.VERBOSE, this.first, this.second)
}

fun Pair<String, Throwable>.i() {
    prepareLog(Log.INFO, this.first, this.second)
}

fun Pair<String, Throwable>.w() {
    prepareLog(Log.WARN, this.first, this.second)
}

fun Pair<String, Throwable>.wtf() {
    prepareLog(Log.ASSERT, this.first, this.second)
}

private fun prepareLog(priority: Int, message: String, tr: Throwable?) {
    val currentTag = sTagMap.get()
    val tag = currentTag ?: tag() ?: sTag
    if (currentTag != null) {
        sTagMap.remove()
    }
    log(priority, tag, message, tr)
}

private fun log(priority: Int, tag: String?, message: String, tr: Throwable?) {
    fun androidLog(priority: Int, tag: String?, message: String, tr: Throwable?) {
        when (priority) {
            Log.ASSERT -> Log.wtf(tag, message, tr)
            Log.ERROR -> Log.e(tag, message, tr)
            Log.WARN -> Log.w(tag, message, tr)
            Log.INFO -> Log.i(tag, message, tr)
            Log.DEBUG -> Log.d(tag, message, tr)
            Log.VERBOSE -> Log.v(tag, message, tr)
        }
    }
    if (message.length <= MAX_LOG_LENGTH) {
        androidLog(priority, tag, message, tr)
        return
    }
    var i = 0
    val length = message.length
    while (i < length) {
        var newline = message.indexOf('\n', i)
        newline = if (newline != -1) newline else length
        do {
            val end = Math.min(newline, i + MAX_LOG_LENGTH)
            val part = message.substring(i, end)
            androidLog(priority, tag, part, null)
            i = end
        } while (i < newline)
        i++
    }
}

private fun tag(): String? {
    fun createStackElementTag(element: StackTraceElement): String? {
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
            tag
        } else {
            tag.substring(0, MAX_TAG_LENGTH)
        }
    }
    val fqcnIgnore = listOf("LogKTX.kt")
    val tag = Throwable().stackTrace.first { it.fileName !in fqcnIgnore }.let { createStackElementTag(it) }
    return tag
}