package com.hjkl.query

import android.graphics.Bitmap


interface IAlbumArtQuery {
    fun getAlbumArt(songId: Int, albumId: Int): Bitmap?
}