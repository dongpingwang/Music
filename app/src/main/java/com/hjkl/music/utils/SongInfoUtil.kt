package com.hjkl.music.utils

import com.hjkl.comm.FileUtil
import java.io.File


object SongInfoUtil {

    fun isSQ(bitrate: Int): Boolean {
        return bitrate >= 320_000
    }

    fun isHQ(bitrate: Int): Boolean {
        return bitrate >= 128_000 && bitrate < 320_000
    }

    // lrc默认在歌曲文件目录下，文件名：“歌曲文件名.lrc”
    fun getPresetLyricPath(songPath: String): String? {
        val lyricPath = FileUtil.getFolderPath(songPath) + File.separator + FileUtil.getFileName(
            songPath,
            false
        ) + ".lrc"
        if (File(lyricPath).exists()) {
            return lyricPath
        } else {
            return null
        }
    }

    // 专辑封面默认在歌曲文件目录下，文件名：“cover.jpg”/“cover.png”
    fun getPresetAlbumCoverPath(songPath: String): String? {
        var coverPath = FileUtil.getFolderPath(songPath) + File.separator + "cover.jpg"
        if (File(coverPath).exists()) {
            return coverPath
        }
        coverPath = FileUtil.getFolderPath(songPath) + File.separator + "cover.png"
        if (File(coverPath).exists()) {
            return coverPath
        }
        return null
    }

    // 歌手封面默认在歌曲文件目录下，文件名：“art.jpg”/“art.png”
    fun getPresetArtistCoverPath(songPath: String): String? {
        var coverPath = FileUtil.getFolderPath(songPath) + File.separator + "art.jpg"
        if (File(coverPath).exists()) {
            return coverPath
        }
        coverPath = FileUtil.getFolderPath(songPath) + File.separator + "art.png"
        if (File(coverPath).exists()) {
            return coverPath
        }
        return null
    }

    // Cue默认在歌曲文件目录下，文件名：“歌曲文件名.cue”
    fun getPresetCuePath(songPath: String): String? {
        val lyricPath = FileUtil.getFolderPath(songPath) + File.separator + FileUtil.getFileName(
            songPath,
            false
        ) + ".cue"
        if (File(lyricPath).exists()) {
            return lyricPath
        } else {
            return null
        }
    }
}