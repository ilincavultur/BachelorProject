package com.example.digitaldiaryba.ui.saved_presentations.video_playback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha.medium

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Shapes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.digitaldiaryba.R

// START https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideoCard.kt
@Composable
fun VideoCard(
    videoItem: VideoItem,
    isPlaying: Boolean,
    exoPlayer: androidx.media3.exoplayer.ExoPlayer,
    onClick: (Int) -> Unit
) {
    var isPlayerUiVisible by remember { mutableStateOf(false) }
    val isPlayButtonVisible = if (isPlayerUiVisible) true else !isPlaying

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaying) {
                VideoPlayer(exoPlayer) { uiVisible ->
                    isPlayerUiVisible = when {
                        isPlayerUiVisible -> uiVisible
                        else -> true
                    }
                }
            } else {
                VideoThumbnail(videoItem.thumbnail)
            }
            if (isPlayButtonVisible) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                        .clickable { onClick(videoItem.id) }
                    ,
                    tint = Color.White
                )
            }
        }
    }
}
// START https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideoCard.kt