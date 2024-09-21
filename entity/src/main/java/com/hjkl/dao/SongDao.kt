package com.hjkl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hjkl.entity.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM song")
    fun getAll(): List<Song>

    @Query("SELECT * FROM song Where duration > 60000")
    fun getAllFilterShortDuration(): List<Song>

    @Insert
    fun insertAll(song: List<Song>)

    @Query("DELETE FROM song")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song)

    @Delete
    fun delete(song: Song)
}