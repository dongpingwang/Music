package com.hjkl.music.data

import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.model.Lyric
import com.hjkl.music.parser.LyricParser
import com.hjkl.player.media3.PlayerProxy
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

object LyricStatePublisher {

    private const val tag = "LyricStateProvider"

    private val scope = CoroutineScope(CoroutineName(tag))
    private val _curLyricState = MutableStateFlow<Lyric?>(null)

    val curLyricState = _curLyricState

    private val playSongChangedListener = object : (Song?) -> Unit {
        override fun invoke(song: Song?) {
            fetchLyric(song)
        }
    }

    fun init() {
        "init".d()
        PlayerProxy.registerPlaySongChangedListener(playSongChangedListener)
    }

    fun destroy() {
        "destroy".d()
        PlayerProxy.unregisterPlaySongChangedListener(playSongChangedListener)
    }

    private fun fetchLyric(song: Song?) {
        "start fetch lyric: ${song?.shortLog()}".d()
        if (song == null) {
            "song is null, return null lyric".d()
            _curLyricState.tryEmit(null)
            return
        }
        if (song.songId == _curLyricState.value?.songId && _curLyricState.value?.originLrcText?.isNotEmpty() == true) {
            "song is previous, return saved lyric".d()
            _curLyricState.tryEmit(_curLyricState.value)
            return
        }
        scope.launch(Dispatchers.IO) {
            val lyric = LyricParser.parseLyric(song)
            _curLyricState.tryEmit(lyric)
        }
    }
}