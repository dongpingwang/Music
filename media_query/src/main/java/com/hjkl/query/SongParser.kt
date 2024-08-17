package com.hjkl.query

import com.hjkl.comm.LogTrace
import com.hjkl.comm.createIfNull
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Song


fun List<Song>.parseAlbum(): List<Album> = LogTrace.measureTimeMillis("SongParser#parseAlbum()") {
    val idToAlbumMap = hashMapOf<Int, Album>()
    onEach { song ->
        idToAlbumMap[song.albumId].createIfNull {
            Album(song.albumId, song.album).also {
                idToAlbumMap[song.albumId] = it
            }
        }.addSong(song)
    }
    idToAlbumMap.values.toList()
}


fun List<Song>.parseArtist(): List<Artist> = LogTrace.measureTimeMillis("SongParser#parseArtist()") {
    val idToArtistMap = hashMapOf<Int, Artist>()
    onEach { song ->
        idToArtistMap[song.artistId].createIfNull {
            Artist(song.artistId, song.artist).also {
                idToArtistMap[song.artistId] = it
            }
        }.addSong(song)
    }
    idToArtistMap.values.toList()
}
