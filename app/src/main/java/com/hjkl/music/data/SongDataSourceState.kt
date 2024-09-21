package com.hjkl.music.data

import com.hjkl.entity.Song

data class SongDataSourceState(
    val isFetchCompleted: Boolean = false,// 数据获取完成
    val isExtractCompleted: Boolean = false, // 数据解析完成
    val songs: List<Song>,
    val errorMsg: String?,
    val updateTimeMillis: Long?// 数据获取或者解析完成时间戳
) {
    fun shortLog(): String {
        return "SongDataSourceState(isFetchCompleted=$isFetchCompleted, isExtractCompleted=$isExtractCompleted, songs.size=${songs.size}, errorMsg=$errorMsg, updateTimeMillis=$updateTimeMillis)"
    }
}