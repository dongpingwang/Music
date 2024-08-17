package com.hjkl.music.test

import SongUiState
import com.hjkl.entity.Song

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
    val songUiState =
        SongUiState.Success(
            songs = songs,
            curPage = 0,
            curSong = null,
            isPlaying = false,
        )
}