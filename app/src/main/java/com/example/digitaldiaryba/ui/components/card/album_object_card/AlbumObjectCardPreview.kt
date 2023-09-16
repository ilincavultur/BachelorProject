package com.example.digitaldiaryba.ui.components.card.album_object_card

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.enums.EAlbumCardType

@Composable
fun AlbumObjectCardPreview(
    cardType: EAlbumCardType,
    coverPhotoUri: String,
    presentationCoverPhotoUri: String,
    onMagicWandIconClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
    context: Context
) {
    // START https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }.crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = presentationCoverPhotoUri,
        imageLoader = imageLoader,
    )
    // END https://stackoverflow.com/questions/69340418/how-to-display-video-thumbnail-in-jetpack-compose
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (cardType == EAlbumCardType.PRESENTATIONS) {
            Image(
                painter = painter,
                contentDescription = "video_preview",
                modifier = Modifier
                    .fillMaxSize()
                    .size(
                        Constants.PRESENTATION_CARD_PREVIEW_WIDTH,
                        Constants.PRESENTATION_CARD_PREVIEW_HEIGHT
                    )
                    .aspectRatio(Constants.PRESENTATION_CARD_PREVIEW_WIDTH / Constants.PRESENTATION_CARD_PREVIEW_HEIGHT)

            )
        } else {
            AsyncImage(
                model = coverPhotoUri,
                contentDescription = "album_preview_photo",
                modifier = Modifier
                        .fillMaxSize()
                        .size(Constants.ALBUM_CARD_PREVIEW_WIDTH, Constants.ALBUM_CARD_PREVIEW_HEIGHT)
                        .aspectRatio(Constants.ALBUM_CARD_PREVIEW_WIDTH / Constants.ALBUM_CARD_PREVIEW_HEIGHT),
                contentScale = ContentScale.FillBounds
            )
        }


        if(cardType == EAlbumCardType.ALBUM_FEED) {
            IconButton(
                onClick = {
                    onMagicWandIconClick()
                },
                Modifier.background(
                    color = MaterialTheme.colorScheme.onSecondary
                )
                    .align(Alignment.BottomStart)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_magic_wand),
                    contentDescription = "create_presentation",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        IconButton(
            onClick = {
                onDeleteIconClick()
            },
            Modifier.background(
                color = MaterialTheme.colorScheme.onSecondary
            )
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                contentDescription = "delete",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

    }

}