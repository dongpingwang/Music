package com.hjkl.query

import com.hjkl.comm.FileUtil
import com.hjkl.comm.LogTrace
import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder
import com.hjkl.entity.Song


fun List<Song>.parseAlbum(): List<Album> = LogTrace.measureTimeMillis("SongParser#parseAlbum()") {
    val idToMap = hashMapOf<Int, Album>()
    onEach { song ->
        if (idToMap[song.albumId] == null) {
            idToMap[song.albumId] = Album(song.albumId, song.album)
        }
        idToMap[song.albumId]?.addSong(song)
    }
    idToMap.values.toList()
}


fun List<Song>.parseArtist(): List<Artist> = LogTrace.measureTimeMillis("SongParser#parseArtist()") {
    val idToMap = hashMapOf<Int, Artist>()
    onEach { song ->
        if (idToMap[song.artistId] == null) {
            idToMap[song.artistId] = Artist(song.artistId, song.artist)
        }
        idToMap[song.artistId]?.addSong(song)
    }
    idToMap.values.toList()
}

fun List<Song>.parseFolder(): List<Folder> = LogTrace.measureTimeMillis("SongParser#parseFolder()") {
    val folderPathToMap = hashMapOf<String, Folder>()
    onEach { song ->
        val folderPath = FileUtil.getFolderPath(song.data)
        val folderName = FileUtil.getFolderName(song.data)
        if (folderPathToMap[folderPath] == null) {
            folderPathToMap[folderPath] = Folder(folderName, folderPath)
        }
        folderPathToMap[folderPath]?.addSong(song)
    }
    folderPathToMap.values.toList()
}
