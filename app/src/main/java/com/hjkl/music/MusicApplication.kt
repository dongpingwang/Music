package com.hjkl.music

import coil.ImageLoader
import coil.ImageLoaderFactory
import com.hjkl.comm.BaseApplication
import com.hjkl.music.initializer.Initializers

class MusicApplication : BaseApplication(), ImageLoaderFactory {

    override fun init() {
        Initializers.init(this)
    }

    override fun destroy() {
        Initializers.destroy()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).build()
    }

}
