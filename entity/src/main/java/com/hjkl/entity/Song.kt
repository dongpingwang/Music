package com.hjkl.entity

import android.graphics.Bitmap

data class Song(
    val id: Int,
    var title: String,
    var artist: String,
    val artistId: Int,
    var album: String,
    val albumId: Int,
    val genre: String?,
    val genreId: Int?,
    val data: String,
    val displayName: String,
    val duration: Long,
    val size: Int,
    val year:Int,
    var bitmap: Bitmap? = null // 专辑封面
) {

    fun shortLog(): String {
        return "$id - $title - $data"
    }

    // 专辑封面原始数据
    var originBitmapBytes: ByteArray? = null
}
