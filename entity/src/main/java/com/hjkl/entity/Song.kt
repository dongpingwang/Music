package com.hjkl.entity

import android.graphics.Bitmap

data class Song(
    val id: Long,// id
    var title: String,// 歌曲名
    var artist: String,// 歌手
    val artistId: Int,// 歌手id
    var album: String,// 专辑名
    val albumId: Int,// 专辑id
    val data: String,// 全路径
    val displayName: String,// 文件的名称
    val duration: Long,// 时长
    val size: Int,// 文件大小
    var publishDate: String? = null,// 出版年份
    val bitrate: Int,// 比特率,
    var sampleRate: Int? = null, // 采样率
    var bitsPerSample: Int? = null, // 位数
    var channels: Int? = null,//通道数
    val composer: String?,// 作曲
    var writer: String?, // 作词
    var lyricText: String? = null, // 内嵌歌词
    var albumCoverPath: String? = null, // 专辑封面路径
    var artCoverPath: String? = null,// 歌手封面路径
    var bitmap: Bitmap? = null // 专辑封面，从mmr获取的封面
) {

    fun shortLog(): String {
        return "$id - $title - $data"
    }

    fun getEmptyAlbum(): Album {
        return Album(albumId, album)
    }

    fun getEmptyArtist(): Artist {
        return Artist(artistId, artist)
    }

    // 专辑封面原始数据，从mmr获取的封面
    var originBitmapBytes: ByteArray? = null

    // 当前歌曲是否为cue整轨
    var cueAudio: Any? = null
}
