package com.hjkl.music.parser

import androidx.annotation.WorkerThread
import com.hjkl.comm.closeSafely
import com.hjkl.comm.d
import com.hjkl.music.model.CueAudio
import com.hjkl.music.model.CueTrack
import com.hjkl.music.utils.minSecMillisToMillis
import com.hjkl.music.utils.parseMillisTimeToMmSs
import java.io.FileReader
import java.util.Locale

object CueAudioParser {

    @WorkerThread
    fun parseCue(cueFilePath: String): CueAudio? {
        val fileReader = FileReader(cueFilePath)
        val cueText = fileReader.readText()
        if (cueText.isNullOrEmpty()) {
            return null
        }
        fileReader.closeSafely()
        return parseCueFromText(cueText)
    }

    fun parseCueFromText(cueText: String): CueAudio? {
        try {
            val lineTexts = cueText.split("\n")
            val trackIndex =
                lineTexts.indexOfFirst {
                    it.trim().uppercase(Locale.getDefault()).startsWith("TRACK")
                }
            if (trackIndex < 0) {
                "cue file has not tracks".d()
                return null
            }
            val headerLines = lineTexts.subList(0, trackIndex)
            val trackLines = lineTexts.subList(trackIndex, lineTexts.size)
            var performer = ""
            var title = ""
            headerLines.onEach {
                val text = it.trim()
                val textSplit = text.split(" ", limit = 2)
                if (text.uppercase(Locale.getDefault()).startsWith("PERFORMER")) {
                    performer = textSplit.run {
                        this[this.size - 1].replace("\"", "")
                    }
                }
                if (text.uppercase(Locale.getDefault()).startsWith("TITLE")) {
                    title = textSplit.run {
                        this[this.size - 1].replace("\"", "")
                    }
                }
            }
            if (title.isEmpty() && performer.isEmpty()) {
                "cue file performer/title is all empty".d()
                return null
            }
            val cueTracks = ArrayList<CueTrack>()
            var trackNumber = -1
            var trackTitle = ""
            var trackStartTime = ""
            trackLines.onEachIndexed { index, lineText ->
                val text = lineText.trim()
                val textSplit = text.split(" ")
                if (text.uppercase(Locale.getDefault()).startsWith("TRACK")) {
                    trackNumber = textSplit.run {
                        this[1].toInt()
                    }
                }
                if (text.uppercase(Locale.getDefault()).startsWith("TITLE")) {
                    trackTitle = text.split(" ", limit = 2).run {
                        this[this.size - 1].replace("\"", "")
                    }
                }
                if (text.uppercase(Locale.getDefault()).startsWith("INDEX 01")) {
                    trackStartTime = textSplit.run {
                        this[this.size - 1]
                    }
                    cueTracks.add(
                        CueTrack(
                            trackNumber,
                            trackTitle,
                            trackStartTime,
                            trackStartTime.minSecMillisToMillis()
                        )
                    )
                }
            }
            return CueAudio(performer, title, cueTracks)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getPlayedTrackIndex(progress: Long, cueAudio: CueAudio): Int {
        val index = cueAudio.tracks.indexOfFirst { it.startTime > progress }
        if (index < 0) {
            return cueAudio.tracks.size - 1
        }
        if (index < 1) {
            return 0
        }
        return index - 1
    }

    fun getPlayedTrackTitle(progress: Long, cueAudio: CueAudio): String? {
        val index = cueAudio.tracks.indexOfFirst { it.startTime > progress }
        if (index < 0) {
            return cueAudio.tracks[cueAudio.tracks.size - 1].title
        }
        if (index < 1) {
            return null
        }
        return cueAudio.tracks[index - 1].title
    }

    fun getPlayedTrackNumber(progress: Long, duration: Long, cueAudio: CueAudio): String {
        var index = cueAudio.tracks.indexOfFirst { it.startTime > progress }
        if (index < 0) {
            index = cueAudio.tracks.size
        }
        index -= 1
        val result = StringBuilder()
        result.append("Track")
            .append(" ")
            .append(String.format(Locale.getDefault(), "%02d", cueAudio.tracks[index].trackNumber))
            .append(" ")
            .append(cueAudio.tracks[index].startTime.parseMillisTimeToMmSs())
            .append(" - ")
        if (index < cueAudio.tracks.size - 1) {
            result.append(cueAudio.tracks[index + 1].startTime.parseMillisTimeToMmSs())
        } else {
            result.append(duration.parseMillisTimeToMmSs())
        }
        return result.toString()
    }

    fun getTrackNumber(trackIndex: Int, duration: Long, cueAudio: CueAudio): String {
        val result = StringBuilder()
        result.append("Track")
            .append(" ")
            .append(
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    cueAudio.tracks[trackIndex].trackNumber
                )
            )
            .append(" ")
            .append(cueAudio.tracks[trackIndex].startTime.parseMillisTimeToMmSs())
            .append(" - ")
        if (trackIndex < cueAudio.tracks.size - 1) {
            result.append(cueAudio.tracks[trackIndex + 1].startTime.parseMillisTimeToMmSs())
        } else {
            result.append(duration.parseMillisTimeToMmSs())
        }
        return result.toString()
    }
}