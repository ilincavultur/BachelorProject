package com.example.digitaldiaryba.ui.saved_presentations

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.digitaldiaryba.ui.components.card.presentation_item.PresentationItemCardImg
import com.example.digitaldiaryba.ui.saved_presentations.video_playback.VideoCard
import com.example.digitaldiaryba.ui.saved_presentations.video_playback.VideoItem
import com.example.digitaldiaryba.util.decodeUri
import com.example.digitaldiaryba.util.enums.EMediaType



// START https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideosScreen.kt
@Composable
fun PresentationScreen(
    presentationId: Int,
    viewModel: PresentationViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState()
    viewModel.onEvent(PresentationEvent.LandedOnScreen(presentationId))
    val context = LocalContext.current

    state.value.videos.forEach {
        Log.d(TAG, "PresentationScreen: index of it " + state.value.videos.indexOf(it))
        it.id = state.value.videos.indexOf(it)
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(context) { androidx.media3.exoplayer.ExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()

    val playingItemIndex by viewModel.currentlyPlayingIndex.collectAsState()
    var isCurrentItemVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            listState.visibleAreaContainsItem(playingItemIndex, state.value.buildingInfos)
        }.collect { isItemVisible ->
            isCurrentItemVisible = isItemVisible
        }
    }

    LaunchedEffect(playingItemIndex) {
        if (playingItemIndex == null) {
            exoPlayer.pause()
        } else {
            val video = state.value.videos[playingItemIndex!!]
            exoPlayer.setMediaItem(androidx.media3.common.MediaItem.fromUri(decodeUri(video.mediaUrl)), video.lastPlayedPosition)
            Log.d(TAG, "PresentationScreen: playing uri " + video.mediaUrl)

            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    LaunchedEffect(isCurrentItemVisible) {
        if (!isCurrentItemVisible && playingItemIndex != null) {
            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingItemIndex!!)
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingItemIndex == null) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> exoPlayer.play()
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            exoPlayer.release()
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxHeight()
    ) {
        Row {
            LazyColumn() {
                itemsIndexed(state.value.buildingInfos) { itemIndex, item ->

                    var isImgOnTheRight = false
                    if (itemIndex % 2 != 0) {
                        isImgOnTheRight = true
                    }

                    if (item.type == EMediaType.IMAGE) {
                        PresentationItemCardImg(
                            isImgOnTheRight = isImgOnTheRight,
                            title = if(!item.title.isNullOrEmpty() && !item.title.contains("null")) item.title else { "" },
                            architect = if(!item.architect.isNullOrEmpty() && !item.architect.contains("null")) item.architect else { "" },
                            location = if(!item.location.isNullOrEmpty() && !item.location.contains("null")) item.location else { "" },
                            year = if(!item.year.isNullOrEmpty() && !item.year.contains("null")) item.year else { "" },
                            imgUri = item.imgUri ?: "",
                        )
                    }

                    if (item.type == EMediaType.VIDEO) {
                        val id = getIdx(itemIndex, state.value.buildingInfos, state.value.videos)
                        Log.d(TAG, "i m a video in the lazylist " + id)
                        VideoCard(
                            videoItem = state.value.videos[id],
                            exoPlayer = exoPlayer,
                            isPlaying = id == playingItemIndex,
                            onClick = {
                                viewModel.onPlayVideoClick(exoPlayer.currentPosition, id)
                            }
                        )
                    }
                }
            }
        }
    }

}

private fun LazyListState.visibleAreaContainsItem(
    currentlyPlayedIndex: Int?,
    videos: List<PresentationListItem>
): Boolean {
    return when {
        currentlyPlayedIndex == null -> false
        videos.isEmpty() -> false
        else -> {
            layoutInfo.visibleItemsInfo.map { videos[it.index] }
                .contains(videos[currentlyPlayedIndex])
        }
    }
}

private fun getIdx(
    indexInList: Int,
    buildingInfos: List<PresentationListItem>,
    vidList: List<VideoItem>
) : Int {
    var no = 0
    for (videoItem in vidList) {
        if (buildingInfos[indexInList].imgUri?.equals(videoItem.mediaUrl) == true) {
            return no
        } else {
            no += 1
        }
    }
    return -1
}

// END https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideosScreen.kt