package com.hjkl.music.ui.comm

import com.hjkl.comm.d
import com.hjkl.entity.Song
import com.hjkl.music.data.PlayerManager
import com.hjkl.music.ui.NavigationActions
import com.hjkl.music.ui.comm.dialog.PlaylistDialogActions
import com.hjkl.player.constant.RepeatMode

data class TopBarActions(val onDrawerClicked: () -> Unit)

data class BottomBarActions(
    val onPlayToggle: () -> Unit,
    val onScrollToNext: () -> Unit,
    val onScrollToPrevious: () -> Unit
)

data class ItemActions(
    val onPlayAll: (List<Song>) -> Unit,
    val onItemClicked: (List<Song>, Int) -> Boolean,
    val onPlayClicked: (List<Song>, Int) -> Unit,
    val onAddToQueue: (Song) -> Unit
)

data class PlayerActions(
    val onPlayerPageExpandChanged: (Boolean) -> Unit,
    val onPlayToggle: () -> Unit,
    val onSeekBarValueChange: (Boolean, Long) -> Unit,
    val onRepeatModeSwitch: (RepeatMode) -> Unit,
    val onShuffleModeEnable: (Boolean) -> Unit,
    val onPlayPrev: () -> Unit,
    val onPlayNext: () -> Unit,
)

object ActionHandler {

    private val player = PlayerManager

    val bottomBarActions = BottomBarActions(
        // 点击BottomBar中播放按钮，切换播放状态
        onPlayToggle = { player.togglePlay() },
        // 滑动切歌
        onScrollToNext = { player.playNext() },
        onScrollToPrevious = { player.playPrev() })


    val playerActions = PlayerActions(
        // 切换到播放界面事件分发
        onPlayerPageExpandChanged = { },
        // 切换播放状态
        onPlayToggle = { player.togglePlay() },
        // 调节进度
        onSeekBarValueChange = { isUserSeeking, progressInMillis ->
            player.userInputSeekBar(isUserSeeking, progressInMillis)
        },
        // 切换播放模式
        onRepeatModeSwitch = {
            player.switchRepeatMode(it)
        },
        onShuffleModeEnable = { player.setShuffleEnable(it) },
        // 上一曲
        onPlayPrev = { player.playPrev() },
        // 下一曲
        onPlayNext = { player.playNext() })

    val itemActions = ItemActions(
        // 播放全部
        onPlayAll = { player.playAll(it) },
        // 点击列表条目，进到播放界面
        onItemClicked = { songs, index ->
            player.maybePlayIndex(songs, index)
        },
        // 点击列表条目中的播放按钮
        onPlayClicked = { songs, index ->
            player.maybeTogglePlay(songs, index)
        },
        // 点击列表条目中的添加到下一曲按钮
        onAddToQueue = { player.addToNextPlay(it) })


    val playlistDialogAction = PlaylistDialogActions(onClearQueue = {
        player.clearPlaylist()
    },
        onRepeatModeSwitch = { player.switchRepeatMode(it) },
        onShuffleModeEnable = { player.setShuffleEnable(it) },
        onPlayIndex = {
            player.playIndex(
                songs = player.playerUiState.value.playlist,
                startIndex = it,
                playWhenReady = true
            )
        },
        onRemoveIndex = { player.removeItem(it) })


    lateinit var navigationActions: NavigationActions
        private set

    fun inject(navigationActions: NavigationActions) {
        "inject navigationActions: $navigationActions".d()
        this.navigationActions = navigationActions
    }

}