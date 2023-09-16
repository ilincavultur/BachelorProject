package com.example.digitaldiaryba.ui.album_detail.add_image

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.digitaldiaryba.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun ImagePreviewScreen(
    uri: String
) {
    Column (verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.size(85.dp))
        Row (verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = uri,
                contentDescription = "full_image_preview",
            )
        }
    }
}
