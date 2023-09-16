package com.example.digitaldiaryba.ui.album_feed.clean

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.navigation.Screen
import com.example.digitaldiaryba.ui.album_detail.AlbumDetailsEvent
import com.example.digitaldiaryba.ui.album_detail.AlbumDetailsViewModel
import com.example.digitaldiaryba.ui.album_detail.add_audio.AddAudioDialog
import com.example.digitaldiaryba.ui.components.card.media_object_card.MediaObjectCard
import com.example.digitaldiaryba.util.enums.EMediaCardType
import com.example.digitaldiaryba.util.enums.EMediaType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AlbumDetails(
    navController: NavController,
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
    albumId: Int?,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        val state by viewModel.state.collectAsState()
        viewModel.onEvent(AlbumDetailsEvent.LandedOnScreen(albumId!!))

        if (state.isRecordingAudio) {
            AddAudioDialog(state = state, onEvent = viewModel::onEvent)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {
            IconButton(
                onClick = {
                    navController.navigate(Screen.ImageCaptureScreen.route + "/${albumId}")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_photo),
                    contentDescription = "add_photo",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
            IconButton(
                onClick = {
                    navController.navigate(Screen.VideoCaptureScreen.route + "/${albumId}")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_video),
                    contentDescription = "add_video",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
            IconButton(
                onClick = {
                    viewModel.onEvent(AlbumDetailsEvent.ShowRecordAudioDialog(albumId))
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_audio),
                    contentDescription = "add_audio",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyColumn{
                itemsIndexed(state.medias) { itemIndex, item ->
                    Spacer(modifier = Modifier.size(20.dp))
                    MediaObjectCard(
                        mediaObjectType = item.type,
                        mediaObjectUri = item.uri,
                        imagePlaceholderUri = item.decodedUri,
                        cardType = EMediaCardType.ALBUM_FEED,
                        onImagePlaceholderClick = {
                            // nothing
                        },
                        onVideoPlaceholderClick = {
                            // nothing
                        },
                        onAudioPlaceholderClick = {
                            // nothing
                        },
                        onTextClick = {
                            if (item.type == EMediaType.VIDEO) {
                                navController.navigate(
                                    Screen.VideoPreviewScreen.withStringArgs(
                                        item.uri
                                    )
                                )
                            } else {
                                navController.navigate(Screen.FileDetailScreen.route + "/${item.mediaId}")
                            }
                        },
                        onDeleteIconClick = {
                            viewModel.onEvent(AlbumDetailsEvent.DeleteMediaObject(item, item.filepath))
                        },
                        playIcon = {
                            R.drawable.ic_baseline_play_arrow_24
                        },
                    )
                }
            }
        }

    }
}