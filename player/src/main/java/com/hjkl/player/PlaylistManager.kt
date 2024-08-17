package com.hjkl.player

import com.hjkl.entity.Song


class PlaylistManager {

    private var playlist = arrayListOf<Song>()

    fun setPlaylist(playlist: List<Song>) {
        this.playlist.clear()
        this.playlist.addAll(playlist)
    }

    fun getPlaylist(): List<Song> {
        return playlist
    }
}