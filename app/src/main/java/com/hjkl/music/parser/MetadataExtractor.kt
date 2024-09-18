package com.hjkl.music.parser

import android.graphics.BitmapFactory
import com.hjkl.entity.Song
import com.hjkl.music.utils.SongInfoUtil
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

object MetadataExtractor {

    fun extractMetadata(song: Song) {
        val f = AudioFileIO.read(File(song.data))
        extractCover(song, f.tag)
        extraSampleRate(song, f.audioHeader)
        extraBitsPerSample(song, f.audioHeader)
        extraChannels(song, f.audioHeader)
        extraPublishDate(song, f.tag)
        extraLyric(song, f.tag)
        extraCueAudio(song)
    }

    // 解析封面顺序：1、文件夹中预设封面  2.提取封面
    private fun extractCover(song: Song, tag: Tag) {
        try {
            val albumCoverPath = SongInfoUtil.getPresetAlbumCoverPath(song.data)
            var artCoverPath = SongInfoUtil.getPresetArtistCoverPath(song.data)
            if (albumCoverPath != null) {
                if (artCoverPath == null) {
                    artCoverPath = albumCoverPath
                }
                song.albumCoverPath = albumCoverPath
                song.artCoverPath = artCoverPath
                return
            }
            if (tag.firstArtwork != null && tag.firstArtwork.binaryData != null) {
                song.originBitmapBytes = tag.firstArtwork.binaryData
                song.bitmap = BitmapFactory.decodeByteArray(
                    tag.firstArtwork.binaryData,
                    0,
                    tag.firstArtwork.binaryData.size
                )
            }
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun extraSampleRate(song: Song, audioHeader: AudioHeader) {
        try {
            if (song.sampleRate != null) {
                return
            }
            song.sampleRate = audioHeader.sampleRate.toInt()
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun extraBitsPerSample(song: Song, audioHeader: AudioHeader) {
        try {
            if (song.bitsPerSample != null) {
                return
            }
            song.bitsPerSample = audioHeader.bitsPerSample
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun extraChannels(song: Song, audioHeader: AudioHeader) {
        try {
            if (song.channels != null) {
                return
            }
            song.channels = audioHeader.channels.toInt()
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun extraPublishDate(song: Song, tag: Tag) {
        try {
            if (song.publishDate != null) {
                return
            }
            song.publishDate = tag.getFirst(FieldKey.YEAR)
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun extraLyric(song: Song, tag: Tag) {
        try {
            if (song.lyricText != null) {
                return
            }
            song.lyricText = tag.getFirst(FieldKey.LYRICS)
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun extraCueAudio(song: Song) {
        try {
            val cuePath = SongInfoUtil.getPresetCuePath(song.data)
            if (cuePath.isNullOrEmpty()) {
                return
            }
            val cueAudio = CueAudioParser.parseCue(cuePath)
            song.cueAudio = cueAudio
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }
}