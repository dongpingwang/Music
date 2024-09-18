package com.hjkl.music.model


data class CueTrack(
    val trackNumber: Int,
    val title: String,
    val originStartTime: String,
    val startTime: Long
)

data class CueAudio(
    val performer: String,
    val title: String,
    /*val fileName: String*/
    val tracks: List<CueTrack>
)