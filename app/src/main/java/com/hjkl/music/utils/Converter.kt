package com.hjkl.music.utils

import com.hjkl.entity.Playlist
import com.hjkl.entity.Song

fun Song.toPlaylist(): Playlist {
    return Playlist(
        songId = this.songId,
        title = this.title,
        artist = this.artist,
        artistId = this.artistId,
        album = this.album,
        albumId = this.albumId,
        data = this.data,
        duration = this.duration,
        size = this.size,
        publishDate = this.publishDate,
        bitrate = this.bitrate,
        sampleRate = this.sampleRate,
        bitsPerSample = this.bitsPerSample,
        channels = this.channels,
        composer = this.composer,
        lyricText = this.lyricText,
        albumCoverPath = this.albumCoverPath,
        artCoverPath = this.artCoverPath
    )
}

fun Playlist.toSong(): Song {
    return Song(
        songId = this.songId,
        title = this.title,
        artist = this.artist,
        artistId = this.artistId,
        album = this.album,
        albumId = this.albumId,
        data = this.data,
        duration = this.duration,
        size = this.size,
        publishDate = this.publishDate,
        bitrate = this.bitrate,
        sampleRate = this.sampleRate,
        bitsPerSample = this.bitsPerSample,
        channels = this.channels,
        composer = this.composer,
        lyricText = this.lyricText,
        albumCoverPath = this.albumCoverPath,
        artCoverPath = this.artCoverPath
    )
}

fun List<Song>.toPlaylists(): List<Playlist> {
    return this.map { it.toPlaylist() }
}

fun List<Playlist>.toSongs(): List<Song> {
    return this.map { it.toSong() }
}