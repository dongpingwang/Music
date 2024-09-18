package com.hjkl.music.test

import com.hjkl.entity.Album
import com.hjkl.entity.Artist
import com.hjkl.entity.Folder
import com.hjkl.entity.Song
import com.hjkl.music.data.PlayerUiState
import com.hjkl.music.model.CueAudio
import com.hjkl.music.model.CueTrack
import com.hjkl.music.model.Lyric
import com.hjkl.music.parser.LyricParser
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

    val lyric = Lyric(111, "歌名", lyricText, LyricParser.parseLyricLinesFromText(lyricText))


    val cueFileContent = """
        PERFORMER "Miyuki Nakajima"
        TITLE "美雪集"
        FILE "Miyuki Nakajima[meixueji].wav" WAVE
          TRACK 01 AUDIO
            TITLE "ルージユ(王菲 容易受伤的女人)"
            INDEX 01 00:00:00
          TRACK 02 AUDIO
            TITLE "ひとり上手(邓丽君 漫步人生路)"
            INDEX 00 04:32:62
            INDEX 01 04:34:62
          TRACK 03 AUDIO
            TITLE "やまねこ(汤宝如 绝对是个梦)"
            INDEX 00 08:47:02
            INDEX 01 08:49:02
          TRACK 04 AUDIO
            TITLE "ホーㄙにて(叶倩文 只因有爱)"
            INDEX 00 12:49:40
            INDEX 01 12:51:40
          TRACK 05 AUDIO
            TITLE "あわせ镜(徐小凤 少年人)"
            INDEX 00 17:40:57
            INDEX 01 17:42:57
          TRACK 06 AUDIO
            TITLE "笑つてよエンジ(蔡立儿 生死约)"
            INDEX 00 22:56:42
            INDEX 01 22:58:42
          TRACK 07 AUDIO
            TITLE "仆は青い鸟(梦剧院 我是蓝鸟)"
            INDEX 00 28:13:15
            INDEX 01 28:15:15
          TRACK 08 AUDIO
            TITLE "见返り美人(张智霖 逗我开心吧)"
            INDEX 00 33:40:17
            INDEX 01 33:42:17
          TRACK 09 AUDIO
            TITLE "春なのに(张国荣 第一次)"
            INDEX 00 38:22:27
            INDEX 01 38:24:27
          TRACK 10 AUDIO
            TITLE "あした(李克勤 破晓时分)"
            INDEX 00 43:27:32
            INDEX 01 43:29:32
          TRACK 11 AUDIO
            TITLE "サツボロsnowy(陈红 雪夜)"
            INDEX 00 48:56:55
            INDEX 01 48:58:55
          TRACK 12 AUDIO
            TITLE "シユガー(邝美云 疼我爱我)"
            INDEX 00 54:37:67
            INDEX 01 54:39:67
          TRACK 13 AUDIO
            TITLE "忘れな草をもう一度(林子祥 莫再悲)"
            INDEX 00 61:13:20
            INDEX 01 61:15:20
          TRACK 14 AUDIO
            TITLE "恶女(王菲 若你真爱我)"
            INDEX 00 64:50:15
            INDEX 01 64:52:15

    """.trimIndent()

    val cueAudio = CueAudio(
        "Miyuki Nakajima",
        "美雪集",
        listOf(
            CueTrack(1, "ルージユ(王菲 容易受伤的女人)", "", 0),
            CueTrack(1, "ルージユ(王菲 容易受伤的女人)", "", 0),
            CueTrack(1, "ルージユ(王菲 容易受伤的女人)", "", 0)
        )
    )
}