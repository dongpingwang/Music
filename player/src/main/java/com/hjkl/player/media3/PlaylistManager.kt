package com.hjkl.player.media3

import com.hjkl.entity.Song

class PlaylistManager {

    private val playlistChangedListeners = mutableListOf<(List<Song>) -> Unit>()
    private var playlist = arrayListOf<Song>()

    fun setPlaylist(playlist: List<Song>) {
        this.playlist.clear()
        this.playlist.addAll(playlist)
        notifyPlaylistChanged()
    }

    fun getPlaylist(): List<Song> {
        return playlist
    }

    fun addSong(index: Int, song: Song) {
        playlist.add(index, song)
        notifyPlaylistChanged()
    }

    private fun notifyPlaylistChanged() {
        playlistChangedListeners.onEach { it(ArrayList<Song>().apply { addAll(playlist) }) }
    }

    fun registerPlaylistChangedListener(listener: (List<Song>) -> Unit): Boolean {
        return playlistChangedListeners.contains(listener) || playlistChangedListeners.add(listener)
    }

    fun unregisterPlaylistChangedListener(listener: (List<Song>) -> Unit): Boolean {
        return playlistChangedListeners.remove(listener)
    }
}