package com.hjkl.music.test

import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder
import com.hjkl.entity.Song
import com.hjkl.music.ui.comm.ViewModelState

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
    val songUiState = ViewModelState<Song>(datas = songs)

    val album = Album(111, "专辑1")
    val albums = listOf(album, album, album)
    val albumUiState = ViewModelState<Album>(datas = albums)


    val artist = Artist(111, "歌手1")
    val artists = listOf(artist, artist, artist)
    val artistUiState = ViewModelState<Artist>(datas = artists)

    val folder = Folder("文件夹1", "/mnt/sdcard/文件夹1")
    val folders = listOf(folder, folder, folder)
    val folderUiState = ViewModelState<Folder>(datas = folders)
}