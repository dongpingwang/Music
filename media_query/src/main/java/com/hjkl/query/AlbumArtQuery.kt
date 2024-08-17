package com.hjkl.query

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.hjkl.comm.AppUtil
import com.hjkl.comm.LogTrace
import java.io.FileDescriptor

class AlbumArtQuery : IAlbumArtQuery {
    companion object {
        private const val TAG = "AlbumArtQuery"
    }

    override fun getAlbumArt(songId: Int, albumId: Int): Bitmap? = LogTrace.measureTimeMillis("AlbumArtQuery#getAlbumArt()") {
            if (songId < 0 && albumId < 0) {
                return@measureTimeMillis null
            }
            kotlin.runCatching {
                val options = BitmapFactory.Options()
                var pfd: ParcelFileDescriptor? = null
                var fd: FileDescriptor? = null
                if (albumId < 0) {
                    val uri =
                        Uri.parse("content://media/external/audio/media/" + songId + "/albumart")
                    pfd = AppUtil.getContext().contentResolver.openFileDescriptor(uri, "r")
                    if (pfd != null) {
                        fd = pfd.fileDescriptor
                    }
                } else {
                    val uri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"), albumId.toLong()
                    )
                    pfd = AppUtil.getContext().contentResolver.openFileDescriptor(uri, "r")
                    if (pfd != null) {
                        fd = pfd.fileDescriptor
                    }
                }
                options.inSampleSize = 1
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFileDescriptor(fd, null, options)
                options.inJustDecodeBounds = false
                options.inDither = false
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options)
                pfd?.close()
                bitmap
            }.getOrElse {
                // it.printStackTrace()
                null
            }
        }

}