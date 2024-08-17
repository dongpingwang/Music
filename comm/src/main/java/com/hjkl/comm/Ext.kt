package com.hjkl.comm

fun <T> T?.createIfNull(block: () -> T): T {
    return this ?: block()
}