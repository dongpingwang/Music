package com.hjkl.entity


data class Album(val id: Int, val name: String) {

    private val songs = ArrayList<Song>()

    fun getSongs():List<Song> {
        return songs
    }

     fun addSong(song: Song):Boolean {
        return songs.add(song)
    }
}
