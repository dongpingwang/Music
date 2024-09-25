package com.hjkl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hjkl.entity.Favorite

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    fun getAll(): List<Favorite>

    @Query("SELECT * FROM favorite where song_id=:songId")
    fun query(songId: Long):Favorite?

    @Query("DELETE FROM favorite")
    fun deleteAll()

    @Insert
    fun insert(favorite: Favorite)

    @Query("DELETE FROM favorite where song_id=:songId")
    fun delete(songId: Long)
}