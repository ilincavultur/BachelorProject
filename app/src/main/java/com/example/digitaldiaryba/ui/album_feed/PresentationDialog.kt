package com.example.digitaldiaryba.ui.album_feed.clean

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.enums.EDialogButtonType

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentationDialog(
    state: AlbumFeedState,
    onEvent: (AlbumFeedEvent) -> Unit
) {
    Dialog(
        onDismissRequest = {
            onEvent(AlbumFeedEvent.HideCreatePresentationDialog)
        },
    ) {
        Surface(
            modifier = Modifier
                .size(Constants.CHOOSE_DIALOG_WIDTH, Constants.CHOOSE_DIALOG_HEIGHT)
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
                                    id = R.string.choose_presentation_name
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
                                value = state.presentationName,
                                onValueChange = {
                                    onEvent(AlbumFeedEvent.ChoosePresentationName(it))
                                },
                            )
                        }
                        Spacer(modifier = Modifier.size(20.dp))

                    }

                }
                if (state.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Spacer(modifier = Modifier.size(34.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DialogButton(
                        type = EDialogButtonType.CANCEL,
                        onClickAction = {
                            onEvent(AlbumFeedEvent.HideCreatePresentationDialog)
                        },
                    )
                    DialogButton(
                        type = EDialogButtonType.CREATE,
                        onClickAction = {
                            onEvent(AlbumFeedEvent.CreatePresentation)
                        },
                    )
                }
            }
        }
    }
}

