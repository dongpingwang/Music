package com.hjkl.comm

import java.io.Closeable

/**
 * 如果对象为空，则返回默认值
 */
fun <T> T?.getOrDefault(default: T): T {
    return this ?: default
}

/**
 * 分批遍历，如10个条目为一组，遍历到第10/20/...个条目时，则当前一批遍历完成
 */
fun <T> List<T>.onBatchEach(firstBatch: Int, perBatchCount: Int, block: (Int, T, Boolean) -> Unit) {
    if (this.size <= perBatchCount) {
        this.onEachIndexed { index, item ->
            val isFirstBatchFinish = (index + 1) == firstBatch
            val isBatchFinish = (index + 1) == this.size
            block(index, item, isFirstBatchFinish || isBatchFinish)
        }
        return
    }
    this.onEachIndexed { index, item ->
        val isFirstBatchFinish = (index + 1) == firstBatch
        val isBatchFinish = (index + 1) % perBatchCount == 0 || (index + 1) == this.size
        block(index, item, isFirstBatchFinish || isBatchFinish)
    }
}

/**
 * 安全的关闭资源流
 */
fun AutoCloseable?.closeSafely() {
    try {
        this?.close()
    } catch (e: Exception) {
        // ignore exception
    }
}

/**
 * 安全的关闭资源流
 */
fun Closeable?.closeSafely() {
    try {
        this?.close()
    } catch (e: Exception) {
        // ignore exception
    }
}

/**
 * 是否是List中的第一个位置
 */
fun <T> List<T>.isFirstIndex(index: Int): Boolean {
    return 0 == index
}

/**
 * 是否是List中的第一个位置
 */
fun <T> List<T>.isLastIndex(index: Int): Boolean {
    return (this.size - 1) == index
}

/**
 * 为true时执行
 */
fun Boolean?.onTrue(block: () -> Unit): Boolean? {
    if (this == true) {
        block()
    }
    return this
}

/**
 * 为false或者null时执行
 */
fun Boolean?.onFalse(block: () -> Unit): Boolean? {
    if (this == false || this == null) {
        block()
    }
    return this
}