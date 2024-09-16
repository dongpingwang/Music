package com.hjkl.music.utils

import android.graphics.BitmapFactory
import com.hjkl.entity.Song
import com.hjkl.query.AlbumArtQuery
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

object MetadataExtractor {

    private val albumArtQuery by lazy { AlbumArtQuery() }

    fun extractMetadata(song: Song): Song {
        try {
            val f = AudioFileIO.read(File(song.data))
            extractCover(song, f.tag)
            extraSampleRate(song, f.audioHeader)
            extraBitsPerSample(song, f.audioHeader)
            extraChannels(song, f.audioHeader)
            extraPublishDate(song, f.tag)
            extraLyric(song, f.tag)
        } catch (e: Exception) {
            // ignore
        }
        return song
    }

    // 解析封面顺序：1、文件夹中预设封面  2、从MediaProvider查询 3.提取封面
    private fun extractCover(song: Song, tag: Tag): Song {
        val albumCoverPath = SongInfoUtil.getPresetAlbumCoverPath(song.data)
        var artCoverPath = SongInfoUtil.getPresetArtistCoverPath(song.data)
        if (albumCoverPath != null) {
            if (artCoverPath == null) {
                artCoverPath = albumCoverPath
            }
            song.albumCoverPath = albumCoverPath
            song.artCoverPath = artCoverPath
            return song
        }

        if (song.bitmap == null) {
            val bitmap = albumArtQuery.getAlbumArtBitmap(song.id, song.albumId)
            song.bitmap = bitmap
            if (bitmap != null) {
                return song
            }
        }

        if (tag.firstArtwork != null && tag.firstArtwork.binaryData != null) {
            song.originBitmapBytes = tag.firstArtwork.binaryData
            song.bitmap = BitmapFactory.decodeByteArray(
                tag.firstArtwork.binaryData,
                0,
                tag.firstArtwork.binaryData.size
            )
        }
        return song
    }

    private fun extraSampleRate(song: Song, audioHeader: AudioHeader): Song {
        if (song.sampleRate != null) {
            return song
        }
        song.sampleRate = audioHeader.sampleRate.toInt()
        return song
    }

    private fun extraBitsPerSample(song: Song, audioHeader: AudioHeader): Song {
        if (song.bitsPerSample != null) {
            return song
        }
        song.bitsPerSample = audioHeader.bitsPerSample
        return song
    }

    private fun extraChannels(song: Song, audioHeader: AudioHeader): Song {
        if (song.channels != null) {
            return song
        }
        song.channels = audioHeader.channels.toInt()
        return song
    }

    private fun extraPublishDate(song: Song, tag: Tag): Song {
        if (song.publishDate != null) {
            return song
        }
        song.publishDate = tag.getFirst(FieldKey.YEAR)
        return song
    }

    private fun extraLyric(song: Song, tag: Tag): Song {
        if (song.lyricText != null) {
            return song
        }
        song.lyricText = tag.getFirst(FieldKey.LYRICS)
        return song
    }
}