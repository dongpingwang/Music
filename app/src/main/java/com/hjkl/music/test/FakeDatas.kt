package com.hjkl.music.test

import SongViewModel
import com.hjkl.entity.Song
import com.hjkl.music.ui.comm.Defaults

object FakeDatas {
    val song = Song(
        id = 1,
        title = "稻香",
        artist = "周杰伦",
        artistId = 1,
        album = "",
        albumId = 1,
        genre = "",
        genreId = 1,
        data = "",
        displayName = "",
        duration = 1,
        size = 1
    )

    val songs = listOf(song, song, song)
    val songUiState = Defaults.songUiState
}