package com.example.digitaldiaryba.ui.components.card.presentation_item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.digitaldiaryba.util.enums.EMediaType
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun PresentationItemCardImg(
    isImgOnTheRight: Boolean = false,
    title: String = "Eiffel Tower",
    architect: String = "Architect",
    location: String = "Location",
    year: String = "Year built",
    imgUri: String = "",
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row {

            if (isImgOnTheRight) {

                Column(
                    modifier = Modifier.padding(15.dp).weight(0.7f)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900, color = Color(
                                0xFF6F38C5
                            ),
                                fontSize = 24.sp
                            )
                            ) {
                                append(title)
                            }
                        }
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                                append("Architect: ")
                            }
                            append(architect)
                        }
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                                append("Location: ")
                            }
                            append(location)
                        }
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                                append("Year: ")
                            }
                            append(year)
                        }
                    )
                }

                    AsyncImage(
                        model = imgUri,
                        contentDescription = "image_preview",
                        modifier = Modifier
                            .clickable {
                                //onImagePlaceholderClick()
                            }
                            .size(100.dp)
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    )
            } else {

                    AsyncImage(
                        model = imgUri,
                        contentDescription = "image_preview",
                        modifier = Modifier
                            .clickable {
                                //onImagePlaceholderClick()
                            }
                            .size(100.dp)
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    )
                Column(
                    modifier = Modifier.padding(15.dp).weight(0.7f)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900, color = Color(
                                0xFF6F38C5
                            ),
                                fontSize = 24.sp
                            )
                            ) {
                                append(title)
                            }
                        }
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                                append("Architect: ")
                            }
                            append(architect)
                        }
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                                append("Location: ")
                            }
                            append(location)
                        }
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                                append("Year: ")
                            }
                            append(year)
                        }
                    )
                }
            }
        }

    }
}