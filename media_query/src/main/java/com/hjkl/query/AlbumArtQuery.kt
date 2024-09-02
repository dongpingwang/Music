package com.hjkl.query

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.hjkl.comm.AppUtil
import java.io.FileDescriptor

@Deprecated("大概率获取不到专辑图片")
class AlbumArtQuery : IAlbumArtQuery {
    companion object {
        private const val TAG = "AlbumArtQuery"
    }

    override fun getAlbumArtBitmap(songId: Int, albumId: Int): Bitmap? {
        if (songId < 0 && albumId < 0) {
            return null
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
            return bitmap
        }.getOrElse {
            // it.printStackTrace()
            return null
        }
    }
}