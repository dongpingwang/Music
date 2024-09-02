package com.hjkl.player.constant

import androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED

enum class PlayMode {
    LIST,
    REPEAT_ONE,
    SHUFFLE
}

object PlayErrorCode {
    val ERROR_FORMAT_UNSUPPORTED = ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED
}