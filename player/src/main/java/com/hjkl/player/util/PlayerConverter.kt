package com.hjkl.player.util

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hjkl.entity.Song
import com.hjkl.player.constant.PlayMode
import com.hjkl.player.constant.RepeatMode

fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(songId.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                //.setArtworkData(originBitmapBytes)
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
    return find { it.songId.toString() == mediaItem?.mediaId }
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


fun Int.toRepeatMode(): RepeatMode {
    return when (this) {
        0 -> RepeatMode.REPEAT_MODE_OFF
        1 -> RepeatMode.REPEAT_MODE_ALL
        2 -> RepeatMode.REPEAT_MODE_ONE
        else -> RepeatMode.REPEAT_MODE_OFF
    }
}

fun RepeatMode.getValue(): Int {
    return when (this) {
        RepeatMode.REPEAT_MODE_OFF -> 0
        RepeatMode.REPEAT_MODE_ALL -> 1
        RepeatMode.REPEAT_MODE_ONE -> 2
    }
}

