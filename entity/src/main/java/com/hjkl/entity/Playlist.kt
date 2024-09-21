package com.hjkl.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Playlist(
    @PrimaryKey
    val id: Long? = null,// 数据库中主键id
    @ColumnInfo(name = "song_id")
    val songId: Long,// id
    @ColumnInfo(name = "title")
    var title: String,// 歌曲名
    @ColumnInfo(name = "artist")
    var artist: String,// 歌手
    @ColumnInfo(name = "artist_id")
    val artistId: Int,// 歌手id
    @ColumnInfo(name = "album")
    var album: String,// 专辑名
    @ColumnInfo(name = "album_id")
    val albumId: Int,// 专辑id
    @ColumnInfo(name = "data")
    val data: String,// 全路径
    @ColumnInfo(name = "duration")
    val duration: Long,// 时长
    @ColumnInfo(name = "size")
    val size: Int,// 文件大小
    @ColumnInfo(name = "publish_date")
    var publishDate: String? = null,// 出版年份
    @ColumnInfo(name = "bit_rate")
    val bitrate: Int,// 比特率,
    @ColumnInfo(name = "sample_rate")
    var sampleRate: Int? = null, // 采样率
    @ColumnInfo(name = "bits_per_sample")
    var bitsPerSample: Int? = null, // 位数
    @ColumnInfo(name = "channels")
    var channels: Int? = null,//通道数
    @ColumnInfo(name = "composer")
    val composer: String?,// 作曲
    @ColumnInfo(name = "lyric_text")
    var lyricText: String? = null, // 内嵌歌词
    @ColumnInfo(name = "album_cover_path")
    var albumCoverPath: String? = null, // 专辑封面路径
    @ColumnInfo(name = "art_cover_path")
    var artCoverPath: String? = null// 歌手封面路径
)
