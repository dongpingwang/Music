package com.hjkl.music.data

import com.hjkl.comm.LogTrace
import com.hjkl.comm.d
import com.hjkl.comm.onBatchEach
import com.hjkl.db.DatabaseHelper
import com.hjkl.entity.Song
import com.hjkl.music.data.Defaults.defaultSongDataSourceState
import com.hjkl.music.parser.MetadataExtractor
import com.hjkl.query.SongQuery
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



object SongDataSource {

    private val scope = CoroutineScope(CoroutineName("SongDataSource"))
    private val _songDataSourceState = MutableStateFlow(defaultSongDataSourceState)
    val songDataSourceState = _songDataSourceState.asStateFlow()
    private val songQuery = SongQuery()
    private var dataFetcherJob: Job? = null
    private var isExtracting = false

    fun fetchAllSongs(fromUser: Boolean) {
        "fetchAllSongs: fromUser=$fromUser".d()
        onFetchStart()
        if (isExtracting) {
            "正在解析数据中".d()
            scope.launch {
                delay(1500)
                onFetchComplete()
            }
            return
        }
        if (dataFetcherJob != null) {
            dataFetcherJob?.cancel()
            dataFetcherJob = null
        }
        dataFetcherJob = scope.launch(Dispatchers.IO) {
            isExtracting = true
            val dbSongs = doDatabaseFetch()
            val hasDbData = dbSongs.isNotEmpty()
            "dbSongs size: ${dbSongs.size}".d()
            if (hasDbData) {
                if (fromUser) {
                    scope.launch {
                        delay(1500)
                        onFetchComplete()
                    }
                }else {
                    onFetchComplete(dbSongs)
                }
            }
            doMediaProviderFetch().onSuccess {
                " mpSongs size: ${it.size}".d()
                if (!hasDbData) {
                    onFetchComplete(it)
                }
                if (fromUser) {
                    doExtraID3(it)
                    onExtraComplete(it)
                    doUpdateDatabase(it)
                } else {
                    doBatchExtraID3(it) {
                        onExtraCompleting(it)
                    }
                    onExtraComplete(it)
                    doUpdateDatabase(it)
                }
            }.onFailure {
                onError(it.message)
                doClearDatabase()
            }
        }
    }

    private fun onFetchStart() {
        "onFetchStart".d()
        _songDataSourceState.update {
            it.copy(isFetchCompleted = false, isExtractCompleted = false)
        }
    }

    private fun onFetchComplete(songs: List<Song>? = null) {
        "onFetchComplete".d()
        if (songs == null) {
            _songDataSourceState.update {
                it.copy(isFetchCompleted = true)
            }
        }else {
            _songDataSourceState.update {
                it.copy(
                    isFetchCompleted = true,
                    songs = songs,
                    updateTimeMillis = System.currentTimeMillis()
                )
            }
        }

    }

    private fun onError(errorMsg: String?) {
        "onError:$errorMsg".d()
        _songDataSourceState.update {
            it.copy(
                errorMsg = errorMsg, isFetchCompleted = true, isExtractCompleted = true
            )
        }
        isExtracting = false
    }

    private fun onExtraCompleting(songs: List<Song>) {
        "onExtraCompleting".d()
        _songDataSourceState.update {
            it.copy(
                isExtractCompleted = false,
                songs = songs,
                updateTimeMillis = System.currentTimeMillis()
            )
        }
    }

    private fun onExtraComplete(songs: List<Song>) {
        "onExtraComplete".d()
        _songDataSourceState.update {
            it.copy(
                isExtractCompleted = true,
                isFetchCompleted = true,
                songs = songs,
                updateTimeMillis = System.currentTimeMillis()
            )
        }
        isExtracting = false
    }

    private fun doDatabaseFetch(): List<Song> {
        return DatabaseHelper.songDao().getAll()
    }

    private fun doMediaProviderFetch(): Result<List<Song>> {
        return songQuery.getAllSongs()
    }

    private fun doExtraID3(songs: List<Song>) {
        LogTrace.measureTimeMillis("SongViewModel#extractMetadatas()") {
            songs.onEach {
                MetadataExtractor.extractMetadata(it)
            }
        }
    }

    private fun doBatchExtraID3(songs: List<Song>, onBatchFinish: () -> Unit) {
        LogTrace.measureTimeMillis("SongViewModel#extractMetadatas()") {
            songs.onBatchEach(10, 50) { index, item, isBatchFinish ->
                MetadataExtractor.extractMetadata(item)
                if (isBatchFinish) {
                    onBatchFinish()
                }
            }
        }
    }

    private fun doClearDatabase() {
        DatabaseHelper.songDao().deleteAll()
    }

    private fun doUpdateDatabase(songs: List<Song>) {
        DatabaseHelper.songDao().deleteAll()
        DatabaseHelper.songDao().insertAll(songs)
    }
}