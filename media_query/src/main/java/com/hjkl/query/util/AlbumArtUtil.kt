package com.hjkl.query.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.hjkl.comm.LogTrace


object AlbumArtUtil {

    fun extractAlbumArtBitmap(filePath: String): Pair<ByteArray?,Bitmap?>? = LogTrace.measureTimeMillis("AlbumArtUtil#getAlbumArtBitmap()//$filePath") {
        val metadataRetriever = MediaMetadataRetriever()
        try {
            metadataRetriever.setDataSource(filePath)
            val embeddedPicture = metadataRetriever.embeddedPicture
            if (embeddedPicture == null) {
                return@measureTimeMillis null
            }
            val result = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
            metadataRetriever.release()
            metadataRetriever.close()
            return@measureTimeMillis Pair(embeddedPicture, result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@measureTimeMillis null
    }
}