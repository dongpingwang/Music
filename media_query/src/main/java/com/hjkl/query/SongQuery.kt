package com.hjkl.query

import android.annotation.SuppressLint
import android.os.Build
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.hjkl.comm.AppUtil
import com.hjkl.comm.LogTrace
import com.hjkl.entity.Song


class SongQuery : ISongQuery {

    companion object {
        private const val TAG = "SongQuery"
    }

    private val albumArtQuery by lazy { AlbumArtQuery() }

    @SuppressLint("Range")
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
                Log.d(TAG, "cursor is null or null data")
            } else {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex(Media._ID)) // id
                    val title = cursor.getString(cursor.getColumnIndex(Media.TITLE)) // 歌曲名
                    val artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST)) // 歌手
                    val artistId = cursor.getInt(cursor.getColumnIndex(Media.ARTIST_ID)) // 歌手id
                    val album = cursor.getString(cursor.getColumnIndex(Media.ALBUM)) // 专辑名
                    val albumId = cursor.getInt(cursor.getColumnIndex(Media.ALBUM_ID)) // 专辑id
                    var genre: String? = null
                    var genreId: Int? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        genre = cursor.getString(cursor.getColumnIndex(Media.GENRE)) // 类型
                        genreId = cursor.getInt(cursor.getColumnIndex(Media.GENRE_ID)) // 类型id
                    }
                    val data = cursor.getString(cursor.getColumnIndex(Media.DATA)) // 全路径
                    val displayName = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)) // 文件的名称
                    val duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION)) // 时长
                    val size = cursor.getInt(cursor.getColumnIndex(Media.SIZE)) // 文件大小
                    val song = Song(
                        id = id,
                        title = title,
                        artist = artist,
                        artistId = artistId,
                        album = album,
                        albumId = albumId,
                        genre = genre,
                        genreId = genreId,
                        data = data,
                        displayName = displayName,
                        duration = duration,
                        size = size,
                    )
                    song.bitmap = albumArtQuery.getAlbumArt(id, albumId)
                    Log.d(TAG, "song from cursor: $song")
                    result.add(song)
                }
            }
            cursor?.close()
            Result.success(result)
        }.getOrElse {
            it.printStackTrace()
            Result.failure<List<Song>>(it)
        }
    }
}

