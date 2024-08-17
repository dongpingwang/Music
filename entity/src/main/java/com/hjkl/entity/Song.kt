package com.hjkl.entity

import android.graphics.Bitmap

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val artistId: Int,
    val album: String,
    val albumId: Int,
    val genre: String?,
    val genreId: Int?,
    val data: String,
    val displayName: String,
    val duration: Int,
    val size: Int,
    var bitmap: Bitmap? = null // 专辑封面
) {

   fun shortLog(): String {
        return "$id - $title - $data"
    }
}
