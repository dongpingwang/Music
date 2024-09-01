package com.hjkl.query.util

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.comm.ifNull
import com.hjkl.entity.Song


fun Song.extraMetadataIfNeed(): Song = LogTrace.measureTimeMillis("MediaMetadataExt#extraMetadataIfNeed") {
    var metadataRetriever: MediaMetadataRetriever? = null
    try {
        metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(this.data)
        if (this.bitmap == null) {
            // "歌曲专辑图片为null，尝试使用MediaMetadataRetriever进行解析".d()
            val embeddedPicture = metadataRetriever.embeddedPicture
            embeddedPicture?.run {
                this@extraMetadataIfNeed.originBitmapBytes = this
                this@extraMetadataIfNeed.bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
            }
        }
//        if (this.title == "<unknown>" || this.title.isEmpty()) {
//            // "歌曲名称为<unknown>或空，尝试使用MediaMetadataRetriever进行解析".d()
//             this.title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).ifNull { this.title }
//        }
//        if (this.artist == "<unknown>" || this.artist.isEmpty()) {
//
//            // "歌曲歌手名称为<unknown>或空，尝试使用MediaMetadataRetriever进行解析".d()
//            this.artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).ifNull { this.artist }
//        }
//        if (this.album == "<unknown>" || this.album.isEmpty()) {
//            // "歌曲歌手名称为<unknown>或空，尝试使用MediaMetadataRetriever进行解析".d()
//            this.album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM).ifNull { this.album }
//        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        metadataRetriever?.release()
        metadataRetriever?.close()
    }
    return@measureTimeMillis this
}