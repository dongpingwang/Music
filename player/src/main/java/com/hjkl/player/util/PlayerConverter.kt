package com.hjkl.player.util

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hjkl.entity.Song
import com.hjkl.player.constant.PlayMode

fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                //.setArtworkData(originBitmapBytes)
                .setGenre(genre)
                .setAlbumTitle(album)
                .build()
        )
        .setUri(data)
        .build()
}


fun List<Song>.toMediaItem(): List<MediaItem> {
    return map { it.toMediaItem() }
}

fun List<Song>.findSong(mediaItem: MediaItem?): Song? {
    return find { it.id.toString() == mediaItem?.mediaId }
}

fun Int.toPlayMode(): PlayMode {
    return when (this) {
        0 -> PlayMode.LIST
        1 -> PlayMode.REPEAT_ONE
        2 -> PlayMode.SHUFFLE
        else -> PlayMode.LIST
    }
}

fun PlayMode.getValue(): Int {
    return when (this) {
        PlayMode.LIST -> 0
        PlayMode.REPEAT_ONE -> 1
        PlayMode.SHUFFLE -> 2
    }
}