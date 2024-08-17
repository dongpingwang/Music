package com.hjkl.player

import androidx.media3.common.MediaItem
import com.hjkl.entity.Song

fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(data)
        .build()
}


fun List<Song>.toMediaItem(): List<MediaItem> {
    return map { it.toMediaItem() }
}

fun List<Song>.findSong(mediaItem: MediaItem?): Song? {
    return find { it.id.toString() == mediaItem?.mediaId }
}