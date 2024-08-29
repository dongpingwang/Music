package com.hjkl.comm

/**
 * 如果对象为空，则创建
 */
fun <T> T?.createIfNull(block: () -> T): T {
    return this ?: block()
}
/**
 * 如果对象为空，则返回默认值
 */
fun <T> T?.getOrDefault(default: T): T {
    return this ?: default
}

/**
 * 分批遍历，如10个条目为一组，遍历到第10/20/...个条目时，则当前一批遍历完成
 */
fun <T> List<T>.onBatchEach(batchCount: Int, block: (Int, T, Boolean) -> Unit) {
    if (this.size <= batchCount) {
        this.onEachIndexed { index, item ->
            val isBatchFinish = (index + 1) == this.size
            block(index, item, isBatchFinish)
        }
        return
    }
    this.onEachIndexed { index, item ->
        val isBatchFinish = (index + 1) % batchCount == 0 || (index + 1) == this.size
        block(index, item, isBatchFinish)
    }
}