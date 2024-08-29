package com.hjkl.player.util

import java.util.Locale

fun Long.parseMillisTimeToMmSs(): String {
    val timeInSec = this / 1000
    val sec = timeInSec % 60
    val min = timeInSec / 60
    val hour = timeInSec / 60 / 60

    val result = StringBuilder()
    if (hour > 0) {
        result.append(String.format(Locale.getDefault(), "%02d", hour))
    }
    result.append(String.format(Locale.getDefault(), "%02d", min))
    result.append(":")
    result.append(String.format(Locale.getDefault(), "%02d", sec))
    return result.toString()
}
