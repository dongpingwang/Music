package com.hjkl.query

import com.hjkl.entity.Song


interface ISongQuery {

    fun getAllSongs():Result<List<Song>>
}