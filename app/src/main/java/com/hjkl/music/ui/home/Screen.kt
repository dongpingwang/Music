package com.hjkl.music.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.hjkl.comm.ResUtil
import com.hjkl.music.R

enum class Screen(val text: String, val icon: ImageVector) {
    Home(
        ResUtil.getString(R.string.song_title),
        Icons.Default.Home
    ),
    ALBUM(
        ResUtil.getString(R.string.album_title),
        Icons.Default.Album
    ),
    ARTIST(
        ResUtil.getString(R.string.artist_title),
        Icons.Default.PeopleAlt
    ),
    FOLDER(
        ResUtil.getString(R.string.folder_title),
        Icons.Default.Folder
    ),
    FAVORITE(
        ResUtil.getString(R.string.favorite_title),
        Icons.Default.Favorite
    ),
    MYLIST(
        ResUtil.getString(R.string.mylist_title),
        Icons.Default.ListAlt
    ),
    SETTING(ResUtil.getString(R.string.setting_title), Icons.Default.Settings)
}