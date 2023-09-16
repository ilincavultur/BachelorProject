package com.example.digitaldiaryba.ui.album_feed.clean

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.album_feed.AlbumFeedEvent
import com.example.digitaldiaryba.ui.album_feed.AlbumFeedState
import com.example.digitaldiaryba.ui.components.dialog.DialogButton
import com.example.digitaldiaryba.util.*
import com.example.digitaldiaryba.util.enums.EDialogButtonType
import com.example.digitaldiaryba.util.enums.EMediaType

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDialog(
    state: AlbumFeedState,
    onEvent: (AlbumFeedEvent) -> Unit,
) {
    var coverPhotoUri = rememberSaveable{
        mutableStateOf("")
    }
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {

            val input = uri.let { context.contentResolver.openInputStream(it) } ?: return@rememberLauncherForActivityResult
            val outputFile = createFile(context, EMediaType.IMAGE)
            input.copyTo(outputFile.outputStream())
            coverPhotoUri.value = outputFile.absolutePath

            return@rememberLauncherForActivityResult
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Dialog(
        onDismissRequest = {
            onEvent(AlbumFeedEvent.HideCreateAlbumDialog)
        },
    ) {
        val coroutineScope = rememberCoroutineScope()
        var imageUri by remember { mutableStateOf<String?>("") }
        Surface(
            modifier = Modifier
                .size(Constants.CHOOSE_ALBUM_DIALOG_WIDTH, Constants.CHOOSE_ALBUM_DIALOG_HEIGHT)
                .background(
                    MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Spacer(modifier = Modifier.size(39.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    Column {
                        Row {
                            Text(
                                text = stringResource(
                                    id = R.string.choose_album_name
                                ),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Row {
                            OutlinedTextField(
                                textStyle = TextStyle(
                                    color = Color.Black
                                ),
                                maxLines = 1,
                                singleLine = true,
                                value = state.albumName,
                                onValueChange = {
                                    onEvent(AlbumFeedEvent.ChooseAlbumName(it))
                                },
                            )
                        }
                        Spacer(modifier = Modifier.size(20.dp))

                        Row {
                            Text(
                                text = stringResource(
                                    id = R.string.choose_album_description
                                ),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Row {
                            OutlinedTextField(
                                textStyle = TextStyle(
                                    color = Color.Black
                                ),
                                maxLines = 1,
                                singleLine = true,
                                value = state.albumDescription,
                                onValueChange = {
                                    onEvent(AlbumFeedEvent.ChooseAlbumDescription(it))
                                },
                            )
                        }

                        Spacer(modifier = Modifier.size(20.dp))
                        Row {
                            Text(
                                text = stringResource(
                                    id = R.string.choose_album_cover_photo
                                ),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Row {
                            IconButton(onClick = {
                                pickMedia.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.ic_add_photo
                                    ),
                                    contentDescription = "Add Cover Photo"
                                )
                            }

                            if (coverPhotoUri.value != "") {
                                onEvent(AlbumFeedEvent.ChooseAlbumCoverPhotoUri(coverPhotoUri.value))
                                Text(
                                    text = coverPhotoUri.value,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.size(34.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DialogButton(
                        type = EDialogButtonType.CANCEL,
                        onClickAction = {
                            onEvent(AlbumFeedEvent.HideCreateAlbumDialog)
                        }
                    )
                    DialogButton(
                        type = EDialogButtonType.CREATE,
                        onClickAction = {
                            onEvent(AlbumFeedEvent.CreateAlbum)
                        }
                    )
                }
            }
        }
    }
}