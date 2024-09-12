package com.hjkl.music.ui.comm

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import com.hjkl.music.R

@Composable
fun AlbumImage(
    data: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeHolderImage: Int? = R.drawable.default_audio_art,
    errorImage: Int? = R.drawable.default_audio_art
) {
    Box(modifier = modifier) {
        val imageLoaderBuilder = ImageLoader.Builder(LocalContext.current)
            .crossfade(false)
            .diskCachePolicy(CachePolicy.DISABLED)

        if (placeHolderImage != null) {
            imageLoaderBuilder.placeholder(placeHolderImage)
        }
        if (errorImage != null) {
            imageLoaderBuilder.error(errorImage)
        }
        AsyncImage(
            model = data,
            contentDescription = contentDescription,
            imageLoader = imageLoaderBuilder.build(),
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}
