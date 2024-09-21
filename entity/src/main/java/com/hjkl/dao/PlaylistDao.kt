package com.hjkl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hjkl.entity.Playlist

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist")
    fun getAll(): List<Playlist>


    @Insert
    fun insertAll(playlists: List<Playlist>)

    @Query("DELETE FROM playlist")
    fun deleteAll()
}