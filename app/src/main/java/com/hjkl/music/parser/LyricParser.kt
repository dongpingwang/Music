package com.hjkl.music.parser

import androidx.annotation.WorkerThread
import com.hjkl.comm.closeSafely
import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.model.Lyric
import com.hjkl.music.model.LyricLine
import com.hjkl.music.utils.SongInfoUtil
import java.io.FileReader
import java.util.regex.Pattern
import kotlin.math.pow

object LyricParser {

    private val timeRegex = "^\\[[0-9]{1,3}(:[0-9]{1,3})(\\.[0-9]{1,3})]"
    private val timeRegexPattern = Pattern.compile(timeRegex)

    @WorkerThread
    fun parseLyric(song: Song): Lyric? {
        if (song.lyricText != null && song.lyricText?.isNotEmpty() == true) {
            "使用内置歌词文本进行解析".d()
            return parseLyric(song.id, song.title, song.lyricText!!)
        }
        val lyricPath = SongInfoUtil.getPresetLyricPath(song.data)
        "lyricPath: $lyricPath".d()
        if (lyricPath.isNullOrEmpty()) {
            "歌词文件不存在:$lyricPath".d()
            return null
        }
        val fileReader = FileReader(lyricPath)
        val lyricText = fileReader.readText()
        if (lyricText.isEmpty()) {
            "歌词文件内容为空:$lyricPath".d()
            return null
        }
        fileReader.closeSafely()
        return parseLyric(song.id, song.title, lyricText)
    }

    private fun parseLyric(songId: Long, songName: String, lyricText: String): Lyric {
        return Lyric(songId, songName, lyricText, parseLyricLinesFromText(lyricText))
    }

    private fun parseLyricLinesFromText(lyricText: String): List<LyricLine> {
        val lineTexts = lyricText.split("\n")
        val result = ArrayList<LyricLine>()
        lineTexts.onEach {
            val matcher = timeRegexPattern.matcher(it)
            while (matcher.find()) {
                val startIndex = matcher.start()
                val endIndex = matcher.end()
                val timeText = it.substring(startIndex, endIndex)
                val content = it.substring(endIndex).trim()
                val line = LyricLine(calcTime(timeText), content)
                result.add(line)
            }
        }
        return result
    }

    private fun calcTime(timeText: String): Long {
        val text = timeText.replace("[", "").replace("]", "")
        val dotIndex = text.lastIndexOf(".")
        val millis = text.substring(dotIndex + 1).toLong()
        var timeMillis = millis
        text.substring(0, dotIndex).split(":").reversed().onEachIndexed { i, content ->
            timeMillis += (content.toLong() * 60.0.pow(i.toDouble()) * 1000).toLong()
        }
        return timeMillis
    }
}