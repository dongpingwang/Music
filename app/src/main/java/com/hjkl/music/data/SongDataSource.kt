package com.hjkl.music.data

import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.comm.onBatchEach
import com.hjkl.comm.onFalse
import com.hjkl.comm.onTrue
import com.hjkl.entity.Song
import com.hjkl.music.data.Defaults.defaultSongDataSourceState
import com.hjkl.query.SongQuery
import com.hjkl.query.util.extraMetadataIfNeed
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SongDataSourceState(
    val isLoading: Boolean,
    val songs: List<Song>,
    val errorMsg: String?,
    val updateTimeMillis: Long?// 数据获取或者解析完成时间戳
) {
    fun shortLog(): String {
        return "SongDataSourceState(isLoading=$isLoading, songs.size=${songs.size}, errorMsg=$errorMsg, updateTimeMillis=$updateTimeMillis)"
    }
}

class SongDataSource {
    companion object {
        private val dataSource by lazy { SongDataSource() }
        fun get(): SongDataSource {
            return dataSource
        }
    }


    private val scope = CoroutineScope(CoroutineName("SongDataSource"))
    private val _songDataSourceState = MutableStateFlow(defaultSongDataSourceState)
    val songDataSourceState = _songDataSourceState.asStateFlow()
    private val songQuery = SongQuery()

    /**
     * 数据加载解析流程：
     * 1.从MediaContentProvider中获取歌曲列表，不包含专辑封面，完成后更新UI
     * 2.通过MediaMetadataRetriever解析专辑封面，完成后更新UI
     * 3.MediaMetadataRetriever解析比较耗时，eg：20ms/歌曲，每解析50个歌曲进行分批刷新
     */
    fun fetchAllSongs(fromUser: Boolean) {
        "fetchAllSongs: fromUser=$fromUser".d()
        scope.launch(Dispatchers.IO) {
            _songDataSourceState.update { it.copy(isLoading = true) }
            "start fetch data from mediaprovider".d()
            songQuery.getAllSongs()
                .onSuccess { songs ->
                    "finish fetch data from mediaprovider".d()
                    fromUser.onFalse {
                        _songDataSourceState.update {
                            it.copy(
                                songs = songs,
                                updateTimeMillis = System.currentTimeMillis()
                            )
                        }
                        "start extract data from mmr".d()
                        LogTrace.measureTimeMillis("SongViewModel#extraMetadataIfNeed()") {
                            songs.onBatchEach(10, 50) { index, item, isBatchFinish ->
                                item.extraMetadataIfNeed()
                                isBatchFinish.onTrue {
                                    _songDataSourceState.update {
                                        it.copy(
                                            isLoading = (index + 1) != songs.size,
                                            songs = songs,
                                            updateTimeMillis = System.currentTimeMillis()
                                        )
                                    }
                                }
                            }
                        }
                        "finish extract data from mmr".d()
                    }.onTrue {
                        "start extract data from mmr".d()
                        LogTrace.measureTimeMillis("SongViewModel#extraMetadataIfNeed()") {
                            songs.onEach {
                                it.extraMetadataIfNeed()
                            }
                        }
                        _songDataSourceState.update {
                            it.copy(
                                isLoading = false,
                                songs = songs,
                                updateTimeMillis = System.currentTimeMillis()
                            )
                        }
                        "finish extract data from mmr".d()
                    }
                }.onFailure { throwable ->
                    _songDataSourceState.update { it.copy(errorMsg = throwable.message) }
                }
        }
    }
}