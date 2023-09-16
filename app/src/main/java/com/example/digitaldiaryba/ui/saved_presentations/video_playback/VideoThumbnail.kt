package com.example.digitaldiaryba.ui.saved_presentations.video_playback

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.VideoFrameDecoder
import com.example.digitaldiaryba.ui.navigation.Screen
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.decodeUri

@Composable
fun VideoThumbnail(url: String) {
    // START https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(VideoFrameDecoder.Factory())
        }.crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = decodeUri(url),
        imageLoader = imageLoader,
    )
    // END https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
    Image(
        painter = painter,
        contentDescription = "video_preview",
        modifier = Modifier
            .size(
                Constants.FEED_CARD_MEDIA_WIDTH,
                Constants.FEED_CARD_MEDIA_HEIGHT
            )
            .aspectRatio(Constants.FEED_CARD_MEDIA_WIDTH / Constants.FEED_CARD_MEDIA_HEIGHT)

    )
}