package com.hjkl.music.data

import com.hjkl.entity.Song
import com.hjkl.query.SongQuery

class GetAllSongsUseCase(
    private val dataSource: SongQuery,
) : SongRepository {

    override suspend fun getAllSongs(): Result<List<Song>>  {
        return dataSource.getAllSongs()
    }
}