package com.hjkl.music.test

import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder
import com.hjkl.entity.Song
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.ui.comm.ViewModelState
import com.hjkl.player.constant.RepeatMode

object FakeDatas {

    val playerUiState = PlayerUiState(
        curSong = null,
        curPlayIndex = 0,
        isPlaying = false,
        progressInMs = 0L,
        repeatMode = RepeatMode.REPEAT_MODE_OFF,
        shuffled = false,
        playerErrorMsgOnce = null,
        toast = null,
        randomNoPlayContentDesc = "" /*ResUtil.getString(randomNoPlayDescRes())*/,
        playlist = emptyList()
    )

    val song = Song(
        id = 1,
        title = "稻香",
        artist = "周杰伦",
        artistId = 1,
        album = "",
        albumId = 1,
        data = "",
        displayName = "",
        duration = 1,
        size = 1,
        publishDate = "1999-01-01",
        bitrate = 128,
        composer = "",
        writer = ""
    )
    val songs = listOf(song, song, song)
    val songUiState = ViewModelState<Song>(datas = songs)

    val album = Album(111, "专辑1").apply {
        addSong(song)
        addSong(song)
        addSong(song)
        addSong(song)
    }
    val albums = listOf(album, album, album)
    val albumUiState = ViewModelState<Album>(datas = albums)


    val artist = Artist(111, "歌手1").apply {
        setAlbums(albums)
        addSong(song)
        addSong(song)
        addSong(song)
        addSong(song)
    }
    val artists = listOf(artist, artist, artist)
    val artistUiState = ViewModelState<Artist>(datas = artists)

    val folder = Folder("文件夹1", "/mnt/sdcard/文件夹1").apply {
        addSong(song)
        addSong(song)
        addSong(song)
        addSong(song)
    }
    val folders = listOf(folder, folder, folder)
    val folderUiState = ViewModelState<Folder>(datas = folders)


    val lyricText = """
        [00:32.52] 路上行人匆匆過
        [00:37.20] 沒有人會回頭看一眼
        [00:45.50] 我只是個流著淚
        [00:50.37] 走在大街上的陌生人
        [00:58.66] 如今我對你來說
        [01:03.55] 也只不過是個陌生人
        [01:11.61] 看見我走在雨裏
        [01:16.58] 你也不會再為我心疼
        [01:24.81] 曾經心痛為何變成陌生
        [01:31.53] 我只想要和你一起飛翔
        [01:38.05] 管它地久天長 只要曾經擁有
        [01:44.55] 我是真的這麼想
        [01:51.02] 曾經心疼為何變成陌生
        [01:57.55] 愛情就像人生不能重來
        [02:04.29] 這些道理我懂 可是真正面對
        [02:10.83] 教我如何放得下
        [02:21.49] 
        [02:50.16] 如今我對你來說
        [02:54.85] 也只不過是個陌生人
        [03:03.39] 看見我走在雨裏
        [03:08.22] 你也不會再為我心疼
        [03:16.36] 曾經心痛為何變成陌生
        [03:22.88] 我只想要和你一起飛翔
        [03:29.65] 管它地久天長 只要曾經擁有
        [03:36.16] 我是真的這麼想
        [03:42.50] 曾經心疼為何變成陌生
        [03:49.17] 愛情就像人生不能重來
        [03:55.64] 這些道理我懂 可是真正面對
        [04:02.31] 教我如何放得下
        [04:11.47] 
    """.trimIndent()
}