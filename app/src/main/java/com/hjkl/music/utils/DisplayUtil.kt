package com.hjkl.music.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hjkl.comm.getOrDefault
import com.hjkl.music.R

object DisplayUtil {

    @Composable
    fun getDisplayTitle(title: String?): String {
        return title.getOrDefault(stringResource(R.string.unknown_song))
    }

    @Composable
    fun getDisplayArtist(artist: String?): String {
        return artist.getOrDefault(stringResource(R.string.unknown_artist))
    }
}