package com.hjkl.music.data

import com.hjkl.entity.Song
import com.hjkl.query.SongQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetAllSongsUseCase(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: SongQuery,
) : SongRepository {

    override suspend fun getAllSongs(): Result<List<Song>> = withContext(ioDispatcher) {
        return@withContext dataSource.getAllSongs()
    }
}