package com.example.digitaldiaryba.ui.album_detail.add_audio

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.album_detail.AlbumDetailsEvent
import com.example.digitaldiaryba.ui.album_detail.AlbumDetailsState
import com.example.digitaldiaryba.ui.album_detail.add_audio.play.AudioPlayer
import com.example.digitaldiaryba.ui.album_detail.add_audio.record.AudioRecorder
import com.example.digitaldiaryba.ui.components.dialog.DialogButton
import com.example.digitaldiaryba.util.*
import com.example.digitaldiaryba.util.enums.EDialogButtonType

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission", "PrivateResource")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddAudioDialog(
    state: AlbumDetailsState,
    onEvent: (AlbumDetailsEvent) -> Unit,
) {
    val context = LocalContext.current

    val isRecording = remember { mutableStateOf(false) }
    val isPlaying = remember { mutableStateOf(false) }

    val recorder by lazy {
        AudioRecorder(context)
    }

    val player by lazy {
        AudioPlayer(isPlaying)
    }

    val coroutineScope = rememberCoroutineScope()
    var audioUri by rememberSaveable { mutableStateOf<String?>("") }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = { /* ... */ },
        permissionsNotAvailableContent = { /* ... */ }
    ) {
        Dialog(
            onDismissRequest = {
                onEvent(AlbumDetailsEvent.HideRecordAudioDialog)
            },
        ) {
            Surface(
                Modifier
                    .size(Constants.ADD_DIALOG_WIDTH, Constants.ADD_DIALOG_HEIGHT)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondary),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Spacer(modifier = Modifier.size(25.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp),
                        horizontalArrangement = Arrangement.Start

                    ) {
                        Column {
                            Text(
                                text = stringResource(
                                    id = R.string.take_new_audio
                                ),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Row {
                                IconButton(onClick = {
                                    if (!isRecording.value) {
                                        recorder.startRecording(context)
                                        isRecording.value = true
                                    } else {
                                        audioUri = recorder.stopRecording()
                                        isRecording.value = false
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(
                                            id = R.drawable.ic_add_audio
                                        ),
                                        contentDescription = "Audio"
                                    )
                                }

                                if (audioUri.toString().isNotEmpty()) {
                                    Text(
                                        text = audioUri.toString(),
                                        maxLines = 1
                                    )
                                    onEvent(AlbumDetailsEvent.ExtractMetadata(audioUri.toString().toUri()))
                                    onEvent(AlbumDetailsEvent.SetFileDetails(audioUri.toString(), Geolocation()))
                                }

                            }

                            Row {
                                IconButton(onClick = {
                                    if (!isPlaying.value) {
                                        audioUri?.let { player.playRecording(it) }
                                        isPlaying.value = true
                                    } else {
                                        player.stopPlaying()
                                        isPlaying.value = false
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(
                                            id = R.drawable.ic_baseline_play_arrow_24
                                        ),
                                        contentDescription = "Audio"
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DialogButton(
                            type = EDialogButtonType.CANCEL,
                            onClickAction = {
                                onEvent(AlbumDetailsEvent.HideRecordAudioDialog)
                            }
                        )
                        DialogButton(
                            type = EDialogButtonType.ADD,
                            onClickAction = {
                                onEvent(AlbumDetailsEvent.InsertAudioIntoDb)
                            }
                        )
                    }
                }
            }
        }
    }
}