package com.hjkl.music.data

import com.hjkl.comm.d
import com.hjkl.db.DatabaseHelper
import com.hjkl.entity.Song
import com.hjkl.music.utils.toFavorite
import com.hjkl.player.media3.PlayerProxy
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


object FavoriteManager {

    private const val TAG = "FavoriteManager"
    private val scope = CoroutineScope(CoroutineName(TAG))
    private val _curSongFavoriteState = MutableStateFlow(false)
    val curSongFavoriteState = _curSongFavoriteState.asStateFlow()
    private var curSong: Song? = null

    private val playSongChangedListener = object : (Song?) -> Unit {
        override fun invoke(song: Song?) {
            curSong = song
            if (song != null) {
                updateCurSongFavoriteState(song)
            }
        }
    }

    private fun updateCurSongFavoriteState(song: Song) {
        scope.launch(Dispatchers.IO) {
            val favorite = DatabaseHelper.favoriteDao().query(song.songId)
            _curSongFavoriteState.tryEmit(favorite != null)
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

    fun setCurSongCollect(collect: Boolean) {
        curSong?.run {
            scope.launch(Dispatchers.IO) {
                if (collect) {
                    DatabaseHelper.favoriteDao().insert(this@run.toFavorite())
                } else {
                    DatabaseHelper.favoriteDao().delete(this@run.songId)
                }
                _curSongFavoriteState.tryEmit(collect)
            }
        }?:run {
            "curSong is null".d()
        }
    }
}