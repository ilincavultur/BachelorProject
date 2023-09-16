package com.example.digitaldiaryba.ui.components.card.album_object_card

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.digitaldiaryba.util.Constants.ALBUM_CARD_HEIGHT
import com.example.digitaldiaryba.util.Constants.ALBUM_CARD_WIDTH
import com.example.digitaldiaryba.util.enums.EAlbumCardType

@Composable
fun AlbumObjectCard(
    cardType: EAlbumCardType,
    title: String,
    description: String,
    coverPhotoUri: String,
    presentationCoverPhotoUri: String,
    onMagicWandIconClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
    onCardClick: () -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier.size(width = ALBUM_CARD_WIDTH, height = ALBUM_CARD_HEIGHT)
    ) {
        Column {
            AlbumObjectCardContent(title = title, description = description, cardType = cardType, onCardClick = onCardClick)
            AlbumObjectCardPreview(cardType = cardType, onMagicWandIconClick = onMagicWandIconClick, onDeleteIconClick = onDeleteIconClick, coverPhotoUri = coverPhotoUri, presentationCoverPhotoUri = presentationCoverPhotoUri, context = context)
        }
    }
}