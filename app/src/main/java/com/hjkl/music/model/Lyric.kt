package com.hjkl.music.model


data class LyricLine(val startTimeInMillis: Long, val text: String?)

data class Lyric(
    val songId: Long,
    val songName: String,
    val originLrcText: String,
    val lines: List<LyricLine>
)