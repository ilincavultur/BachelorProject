package com.example.digitaldiaryba.ui.file_detail

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.navigation.Screen
//import com.example.digitaldiaryba.ui.album_detail.DescriptionDialog
import com.example.digitaldiaryba.ui.album_detail.add_audio.play.AudioPlayer
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.enums.EMediaType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun FileDetailScreen(
    navController: NavController,
    viewModel: FileDetailViewModel = hiltViewModel(),
    mediaId: Int?,
    snackbarHostState: SnackbarHostState
) {
    var isPlaying = remember { mutableStateOf(false) }
    val player by lazy {
        AudioPlayer(isPlaying)
    }
    viewModel.onEvent(FileDetailsEvent.LandedOnScreen(mediaId!!))
    val state by viewModel.state.collectAsState()
    val mediaObject by viewModel._mediaObject.collectAsState()

    LaunchedEffect(snackbarHostState) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is FileDetailsUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    val infoList = viewModel.infoList.observeAsState()
    val cached by viewModel._cached.collectAsState()
    val buildingInfoId by viewModel._buildingInfoId.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        if (mediaObject.type == EMediaType.IMAGE) {
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    viewModel.onEvent(FileDetailsEvent.FetchLandmarkInfo(refresh = true))
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_refresh_24), contentDescription = "refresh_landmark_info")
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
        } else {
            Spacer(modifier = Modifier.size(50.dp))
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()

        ) {
            mediaObject.let { mediaObj ->
                when(mediaObj.type) {
                    EMediaType.IMAGE -> {
                        AsyncImage(
                            model = mediaObj.decodedUri,
                            contentDescription = "image_preview",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(
                                        Screen.ImagePreviewScreen.withStringArgs(
                                            mediaObj.uri
                                        )
                                    )
                                }
                                .size(
                                    Constants.FEED_CARD_MEDIA_WIDTH,
                                    Constants.FEED_CARD_MEDIA_HEIGHT
                                )
                                .aspectRatio(Constants.FEED_CARD_MEDIA_WIDTH / Constants.FEED_CARD_MEDIA_HEIGHT)
                        )
                    }
                    EMediaType.VIDEO -> {
                        // START https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
                        val imageLoader = ImageLoader.Builder(LocalContext.current)
                            .components {
                                add(VideoFrameDecoder.Factory())
                            }.crossfade(true)
                            .build()

                        val painter = rememberAsyncImagePainter(
                            model = mediaObj.decodedUri,
                            imageLoader = imageLoader,
                        )
                        // END https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
                        Image(
                            painter = painter,
                            contentDescription = "video_preview",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(
                                        Screen.VideoPreviewScreen.withStringArgs(
                                            mediaObj.uri
                                        )
                                    )
                                }
                                .size(
                                    Constants.FEED_CARD_MEDIA_WIDTH,
                                    Constants.FEED_CARD_MEDIA_HEIGHT
                                )
                                .aspectRatio(Constants.FEED_CARD_MEDIA_WIDTH / Constants.FEED_CARD_MEDIA_HEIGHT)

                        )
                    }
                    EMediaType.AUDIO -> {
                        Box(
                            modifier = Modifier
                                .size(
                                    Constants.FEED_CARD_MEDIA_WIDTH,
                                    Constants.FEED_CARD_MEDIA_HEIGHT
                                )
                                .aspectRatio(Constants.FEED_CARD_MEDIA_WIDTH / Constants.FEED_CARD_MEDIA_HEIGHT)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.music_file_placeholder),
                                contentDescription = "image_placeholder",
                                modifier = Modifier.fillMaxSize()
                            )

                            IconButton(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center),
                                onClick = {
                                    isPlaying.value = !isPlaying.value
                                    if (isPlaying.value) {
                                        Log.d(TAG, "FileDetailScreen: isplaying is true")
                                        player.playRecording(mediaObj.decodedUri)

                                    } else {
                                        Log.d(TAG, "FileDetailScreen: isplaying is false")
                                        player.stopPlaying()
                                    }

                                }) {
                                Icon(
                                    modifier = Modifier.size(100.dp),
                                    imageVector =  ImageVector.vectorResource(
                                        id = if (isPlaying.value) {
                                            R.drawable.ic_baseline_pause_24
                                        } else R.drawable.ic_baseline_play_arrow_24
                                    ) ,
                                    contentDescription = "play_stop_audio"
                                )
                            }

                        }

                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Spacer(modifier = Modifier.size(10.dp))
        if (mediaObject.type == EMediaType.IMAGE) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()

            ) {

                Button(onClick = {
                    viewModel.onEvent(FileDetailsEvent.FetchLandmarkInfo(refresh = false))
                },
                    enabled = state.landmarkinfoButtonIsEnabled
                ) {
                    Text(text = "LandmarkInfo")
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = {
                    viewModel.onEvent(FileDetailsEvent.FetchDBpediaInfo)
                },
                    enabled = state.dbpediaButtonIsEnabled
                ) {
                    Text(text = "DbpediaInfo")
                }
            }

            Spacer(modifier = Modifier.size(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.size(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)

            ) {
                LazyColumn() {

                    itemsIndexed(infoList.value!!.infoList.keys.toList()) { index, listItem ->
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                ) {
                                    append(listItem + " ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                ) {
                                    if (!infoList.value!!.infoList[listItem]!!.contains("null")) {
                                        append(infoList.value!!.infoList[listItem]!!)
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }

                }

            }
        }



    }


}

