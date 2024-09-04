package com.hjkl.music

import com.hjkl.comm.BaseApplication
import com.hjkl.music.initializer.Initializers

class MusicApplication : BaseApplication() {

    override fun init() {
        Initializers.init(this)
    }

    override fun destroy() {
        Initializers.destroy()
    }

}
