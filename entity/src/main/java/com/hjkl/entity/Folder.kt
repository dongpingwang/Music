package com.hjkl.entity

import android.graphics.Bitmap

data class Folder(val name: String, val path: String) {
    private val songs = ArrayList<Song>()

    fun getSongs(): List<Song> {
        return songs
    }

    fun addSong(song: Song): Boolean {
        return songs.add(song)
    }

    fun getAlbumArtBitmap(): Bitmap? {
        return songs.firstOrNull { it.bitmap != null }?.bitmap
    }

    fun getSongCount(): Int {
        return songs.size
    }
}
