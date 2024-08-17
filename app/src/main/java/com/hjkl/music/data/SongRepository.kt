package com.hjkl.music.data

import com.hjkl.entity.Song


interface SongRepository {

    suspend fun getAllSongs(): Result<List<Song>>
}