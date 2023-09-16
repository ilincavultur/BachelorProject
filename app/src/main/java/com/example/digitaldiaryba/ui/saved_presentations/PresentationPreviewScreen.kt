package com.example.digitaldiaryba.ui.saved_presentations

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList

// https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
@Composable
fun PresentationPreviewScreen(
    presentationUri: String,
    subtitlePath: String
) {
    Log.d(TAG, "PresentationPreviewScreen: uri " + presentationUri)
    val context = LocalContext.current

    val assetSrtUri = Uri.parse(( "/storage/emulated/0/Android/data/com.example.digitaldiaryba/cache/DigitalDiaryBA/" + subtitlePath))

    val subtitle = MediaItem.SubtitleConfiguration
        .Builder(assetSrtUri)
        .setMimeType(MimeTypes.APPLICATION_SUBRIP)
        .setLanguage("en")
        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
        .build()

    Log.d(TAG, "PresentationPreviewScreen: subti " + subtitle.id)

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context))
            .build().apply {

            val mediaItem = MediaItem.Builder()
                .setUri(presentationUri)
                .setSubtitleConfigurations(ImmutableList.of(subtitle))
                .build()
            setMediaItem(mediaItem)

            prepare()
        }
    }

    DisposableEffect(
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AndroidView(
                    factory = { context ->
                        StyledPlayerView(context).apply {
                            player = exoPlayer
                            subtitleView?.isVisible = true
                        }
                    },
                )
                SubtitleView(context).apply {
                    this.isActivated = true
                    this.setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                }
            }
        }
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}
