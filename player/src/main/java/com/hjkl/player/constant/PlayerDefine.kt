package com.hjkl.player.constant

import androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED

enum class PlayMode {
    LIST,
    REPEAT_ONE,
    SHUFFLE
}

enum class RepeatMode {
    REPEAT_MODE_OFF,
    REPEAT_MODE_ONE,
    REPEAT_MODE_ALL
}

object PlayErrorCode {
    val ERROR_FORMAT_UNSUPPORTED = ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED
}