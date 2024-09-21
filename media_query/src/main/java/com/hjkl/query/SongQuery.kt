package com.hjkl.query

import android.provider.MediaStore.Audio.Media
import com.hjkl.comm.AppUtil
import com.hjkl.comm.LogTrace
import com.hjkl.comm.closeSafely
import com.hjkl.comm.d
import com.hjkl.entity.Song


class SongQuery : ISongQuery {


    override fun getAllSongs(): Result<List<Song>> = LogTrace.measureTimeMillis("SongQuery#getAllSongs()") {
        kotlin.runCatching {
            val cursor = AppUtil.getContext().contentResolver.query(
                Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )
            val result = arrayListOf<Song>()
            if (cursor == null || cursor.count <= 0) {
                "cursor is null or null data: $cursor ${cursor?.count}".d()
            } else {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndex(Media._ID))
                    val title = cursor.getString(cursor.getColumnIndex(Media.TITLE))
                    val artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST))
                    val artistId = cursor.getInt(cursor.getColumnIndex(Media.ARTIST_ID))
                    val album = cursor.getString(cursor.getColumnIndex(Media.ALBUM))
                    val albumId = cursor.getInt(cursor.getColumnIndex(Media.ALBUM_ID))
                    val data = cursor.getString(cursor.getColumnIndex(Media.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION))
                    val size = cursor.getInt(cursor.getColumnIndex(Media.SIZE))
                    val bitrate = cursor.getInt(cursor.getColumnIndex(Media.BITRATE))
                    val composer = cursor.getString(cursor.getColumnIndex(Media.COMPOSER))
                    val song = Song(
                        songId = id,
                        title = title,
                        artist = artist,
                        artistId = artistId,
                        album = album,
                        albumId = albumId,
                        data = data,
                        duration = duration,
                        size = size,
                        bitrate = bitrate,
                        composer = composer
                    )
                    "song from cursor: $song".d()
                    result.add(song)
                }
            }
            cursor.closeSafely()
            Result.success(result)
        }.getOrElse {
            it.printStackTrace()
            Result.failure<List<Song>>(it)
        }
    }
}

