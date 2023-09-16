package com.example.digitaldiaryba.ui.components.card.media_object_card

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.digitaldiaryba.R

import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.Constants.FEED_CARD_MEDIA_HEIGHT
import com.example.digitaldiaryba.util.Constants.FEED_CARD_MEDIA_WIDTH
import com.example.digitaldiaryba.util.enums.EMediaType

@Composable
fun MediaObjectCardPreview(
    mediaObjectType: EMediaType,
    mediaObjectUri: String,
    imagePlaceholderUri: String,
    onImagePlaceholderClick: () -> Unit,
    onVideoPlaceholderClick: () -> Unit,
    onAudioPlaceholderClick: (Boolean) -> Unit,
    playIcon: () -> Int
) {
    var isPlaying = remember { mutableStateOf(false) }
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val context = LocalContext.current
    var visible by rememberSaveable { mutableStateOf(false) }

    // START https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }.crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = imagePlaceholderUri,
        imageLoader = imageLoader,
    )
    // END https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    when(mediaObjectType) {
        EMediaType.IMAGE -> {
            AsyncImage(
                model = imagePlaceholderUri,
                contentDescription = "image_preview",
                modifier = Modifier
                    .clickable {
                        onImagePlaceholderClick()
                    }
                    .size(Constants.FEED_CARD_MEDIA_WIDTH, Constants.FEED_CARD_MEDIA_HEIGHT)
                    .aspectRatio(FEED_CARD_MEDIA_WIDTH / FEED_CARD_MEDIA_HEIGHT)
            )
        }
        EMediaType.VIDEO -> {
            Image(
                painter = painter,
                contentDescription = "video_preview",
                modifier = Modifier.clickable {
                    onVideoPlaceholderClick()
                }
                    .size(Constants.FEED_CARD_MEDIA_WIDTH, Constants.FEED_CARD_MEDIA_HEIGHT)
                    .aspectRatio(FEED_CARD_MEDIA_WIDTH / FEED_CARD_MEDIA_HEIGHT)

            )
        }
        EMediaType.AUDIO -> {
            Box(
                modifier = Modifier
                    .size(Constants.FEED_CARD_MEDIA_WIDTH, Constants.FEED_CARD_MEDIA_HEIGHT)
                    .aspectRatio(FEED_CARD_MEDIA_WIDTH / FEED_CARD_MEDIA_HEIGHT)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.music_file_placeholder),
                    contentDescription = "image_placeholder",
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    modifier = Modifier.fillMaxSize().align(Alignment.Center),
                    onClick = {
                    isPlaying.value = !isPlaying.value
                    onAudioPlaceholderClick(isPlaying.value)
                }) {
                    Icon(
                        modifier = Modifier.size(100.dp),
                        imageVector =  ImageVector.vectorResource(
                            id = playIcon()
                        ) ,
                        contentDescription = "play_stop_audio"
                    )
                }

            }

        }
    }


}