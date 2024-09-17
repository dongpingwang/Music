package com.hjkl.music.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hjkl.comm.FileUtil
import com.hjkl.comm.getOrDefault
import com.hjkl.entity.Song
import com.hjkl.music.R
import com.hjkl.music.model.Lyric

object DisplayUtil {

    @Composable
    fun getDisplayTitle(title: String?): String {
        return title.getOrDefault(stringResource(R.string.unknown_song))
    }

    @Composable
    fun getDisplayArtist(artist: String?): String {
        return artist.getOrDefault(stringResource(R.string.unknown_artist))
    }

    @Composable
    fun getDisplayAlbum(album: String?): String {
        return album.getOrDefault(stringResource(R.string.unknown_album))
    }

    @Composable
    fun getDisplayComposer(composer: String?): String {
        return composer.getOrDefault(stringResource(R.string.unknown_composer))
    }

    fun getPublishDate(year: String?, defValue: String = "-"): String {
        return year.getOrDefault(defValue)
    }

    @Composable
    fun getLyricType(song: Song?, lyric: Lyric?): String {
        if (song?.lyricText != null && song.lyricText?.isNotEmpty() == true) {
            return stringResource(id = R.string.summary_lyric_inner)
        }
        if (lyric?.originLrcText?.isNotEmpty() == true) {
            return stringResource(id = R.string.summary_lyric_file)
        }
        return stringResource(id = R.string.summary_lyric_none)
    }

    fun getAudioTrackInfo(song: Song): String {
        val info = StringBuilder()
        val extendName = FileUtil.getFileExtendName(song.data).toUpperCase()
        info.append(extendName)
        song.sampleRate?.run {
            info.append("  ")
            val kSampleRate = (this.toFloat() / 1000).toString() + "kHz"
            info.append(kSampleRate)
        }
        val bitrate = (song.bitrate / 1000).toString() + "kbps"
        info.append("  ")
        info.append(bitrate)
        song.bitsPerSample?.run {
            val bits = this.toString() + "bit"
            info.append("  ")
            info.append(bits)
        }
        song.channels?.run {
            val channels = this.toString() + "channels"
            info.append("  ")
            info.append(channels)
        }
        return info.toString()
    }
}