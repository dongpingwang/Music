package com.hjkl.query

import com.hjkl.comm.LogTrace
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Song


fun List<Song>.parseAlbum(): List<Album> = LogTrace.measureTimeMillis("SongParser#parseAlbum()") {
    val idToAlbumMap = hashMapOf<Int, Album>()
    onEach { song ->
        if (idToAlbumMap[song.albumId] == null) {
            idToAlbumMap[song.albumId] = Album(song.albumId, song.album)
        }
        idToAlbumMap[song.albumId]?.addSong(song)
    }
    idToAlbumMap.values.toList()
}


fun List<Song>.parseArtist(): List<Artist> = LogTrace.measureTimeMillis("SongParser#parseArtist()") {
    val idToArtistMap = hashMapOf<Int, Artist>()
    onEach { song ->
        if (idToArtistMap[song.artistId] == null) {
            idToArtistMap[song.artistId] = Artist(song.artistId, song.artist)
        }
        idToArtistMap[song.artistId]?.addSong(song)
    }
    idToArtistMap.values.toList()
}
