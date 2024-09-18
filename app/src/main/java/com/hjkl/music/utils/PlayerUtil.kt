package com.hjkl.music.utils

import java.util.Locale
import kotlin.math.roundToInt

fun Long.parseMillisTimeToMmSs(): String {
    val timeInSec = this / 1000
    val sec = timeInSec % 60
    val min = timeInSec / 60 % 60
    val hour = timeInSec / 60 / 60

    val result = StringBuilder()
    if (hour > 0) {
        result.append(String.format(Locale.getDefault(), "%02d", hour))
        result.append(":")
    }
    result.append(String.format(Locale.getDefault(), "%02d", min))
    result.append(":")
    result.append(String.format(Locale.getDefault(), "%02d", sec))
    return result.toString()
}

fun Long.parseMillisTimeToMinutes(): String {
    return String.format(Locale.getDefault(), "%2d mins", (this.toFloat() / 1000 / 60).roundToInt())
}

// 04:34:62 (分：秒：毫秒)
fun String.minSecMillisToMillis(): Long {
    var result = 0L
    val strs = this.split(":")
    result += strs[0].toInt() * 60 * 1000 + strs[1].toInt() * 1000 + strs[2].toInt()
    return result
}