package com.hjkl.db

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hjkl.dao.PlaylistDao
import com.hjkl.dao.SongDao
import com.hjkl.entity.Playlist
import com.hjkl.entity.Song

private const val TAG = "DatabaseHelper"
private const val DATABASE_NAME = "music"
private const val DATABASE_VERSION = 1

@Database(entities = [Song::class, Playlist::class], version = DATABASE_VERSION)
private abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
}


object DatabaseHelper {
    private var appDatabase: AppDatabase? = null
    private lateinit var context: Application

    fun init(context: Application) {
        this.context = context
        Log.d(TAG, "init: ${appDatabase()}")
    }

    private fun appDatabase(): AppDatabase {
        if (appDatabase != null) {
            return appDatabase!!
        } else {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java, DATABASE_NAME
            ).build()
            appDatabase = database
            return database
        }
    }

    fun songDao(): SongDao {
        return appDatabase().songDao()
    }

    fun playlistDao(): PlaylistDao {
        return appDatabase().playlistDao()
    }
}