package com.hjkl.music.parser

import android.graphics.BitmapFactory
import com.hjkl.comm.FileUtil
import com.hjkl.entity.Song
import com.hjkl.music.utils.SongInfoUtil
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.io.FileFilter

object MetadataExtractor {

    fun extractMetadata(song: Song) {
        kotlin.runCatching {
            AudioFileIO.read(File(song.data))
        }.also {
            it.exceptionOrNull()?.printStackTrace()
        }.getOrNull()?.run {
            extractCover(song, this.tag)
            extraSampleRate(song, this.audioHeader)
            extraBitsPerSample(song, this.audioHeader)
            extraChannels(song, this.audioHeader)
            extraPublishDate(song, this.tag)
            extraLyric(song, this.tag)
        }
        extraCueAudio(song)
    }

    // 解析封面顺序：1、文件夹中预设封面  2.提取封面
    private fun extractCover(song: Song, tag: Tag) {
        try {
            if (findLocalCover(song)) {
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

    private fun findLocalCover(song: Song): Boolean {
        try {
            val files = File(FileUtil.getFolderPath(song.data)).listFiles(object : FileFilter {
                override fun accept(file: File?): Boolean {
                    val path = file?.path?.uppercase()
                    if (path == null) {
                        return false
                    }
                    return path.endsWith("COVER.JPG") || path.endsWith("COVER.JPEG") || path.endsWith(
                        "COVER.PNG"
                    ) || path.endsWith("ART.JPG") || path.endsWith("ART.JPEG") || path.endsWith(
                        "ART.PNG"
                    )
                }
            })
            if (files != null && files.isNotEmpty()) {
                val albumCoverPath = files.find {
                    FileUtil.getFileName(it.path, false).uppercase().endsWith("COVER")
                }?.path
                val artCoverPath = files.find {
                    FileUtil.getFileName(it.path, false).uppercase().endsWith("ART")
                }?.path
                if (albumCoverPath != null) {
                    song.albumCoverPath = albumCoverPath
                    song.artCoverPath = artCoverPath ?: albumCoverPath
                    return true
                }
            }
        } catch (e: Exception) {
            // e.printStackTrace()
        }
        return false
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