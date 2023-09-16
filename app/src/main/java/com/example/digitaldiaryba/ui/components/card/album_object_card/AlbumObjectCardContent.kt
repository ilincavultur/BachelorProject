package com.example.digitaldiaryba.ui.components.card.album_object_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.enums.EAlbumCardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumObjectCardContent(
    title: String,
    description: String,
    cardType: EAlbumCardType,
    onCardClick: () -> Unit
) {
    Surface(
        modifier = when(cardType) {
            EAlbumCardType.ALBUM_FEED -> Modifier
                .size(Constants.ALBUM_CARD_CONTENT_WIDTH, Constants.ALBUM_CARD_CONTENT_HEIGHT)
            EAlbumCardType.PRESENTATIONS -> Modifier
                .size(Constants.PRESENTATION_CARD_CONTENT_WIDTH, Constants.PRESENTATION_CARD_CONTENT_HEIGHT)
        },

        onClick = {
            onCardClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = when(cardType) {
                        EAlbumCardType.ALBUM_FEED -> MaterialTheme.colorScheme.onSecondary
                        EAlbumCardType.PRESENTATIONS -> MaterialTheme.colorScheme.onPrimary
                    }
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Start)

            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = when(cardType) {
                            EAlbumCardType.ALBUM_FEED -> MaterialTheme.colorScheme.onBackground
                            EAlbumCardType.PRESENTATIONS -> MaterialTheme.colorScheme.primary
                        },
                        fontSize = 15.sp
                    ),
                    modifier = Modifier
                        .align(
                            Alignment.Top
                        )
                        .padding(start = 20.dp, top = 10.dp)
                )
            }

            if (cardType == EAlbumCardType.ALBUM_FEED) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier.size(width = 204.dp, height = 45.dp)

                    ) {
                        Text(
                            text = description,
                            style = TextStyle(
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier
                        )
                    }

                }
            }

        }
    }
}