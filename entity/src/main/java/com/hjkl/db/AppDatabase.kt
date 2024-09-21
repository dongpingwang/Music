package com.hjkl.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hjkl.dao.SongDao
import com.hjkl.entity.Song

private const val TAG = "DatabaseHelper"
private const val DATABASE_NAME = "music"
private const val DATABASE_VERSION = 1

@Database(entities = [Song::class], version = DATABASE_VERSION)
private abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}

object DatabaseHelper {
    private lateinit var appDatabase: AppDatabase
    
    fun init(context: Context) {
        appDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java, DATABASE_NAME
        ).build()
        Log.d(TAG, "init: $appDatabase")
    }

    fun songDao(): SongDao {
        return appDatabase.songDao()
    }
}