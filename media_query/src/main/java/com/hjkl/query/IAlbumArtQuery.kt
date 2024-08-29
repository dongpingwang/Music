package com.hjkl.query

import android.graphics.Bitmap

@Deprecated("有些机型获取不到专辑图片")
interface IAlbumArtQuery {
    fun getAlbumArt(songId: Int, albumId: Int): Bitmap?
}