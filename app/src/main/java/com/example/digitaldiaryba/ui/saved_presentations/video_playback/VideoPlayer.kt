package com.example.digitaldiaryba.ui.saved_presentations.video_playback

import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.digitaldiaryba.R

// START https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideoPlayer.kt
@Composable
fun VideoPlayer(
    exoPlayer: ExoPlayer,
    onControllerVisibilityChanged: (uiVisible: Boolean) -> Unit
) {
    val context = LocalContext.current
    val playerView = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as PlayerView
        playerView.apply {
            setControllerVisibilityListener(PlayerView.ControllerVisibilityListener {
                onControllerVisibilityChanged(it == View.VISIBLE)
            })
            player = exoPlayer
        }
    }

    AndroidView(
        { playerView },
        Modifier.height(256.dp)
    )
}
// END https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideoPlayer.kt