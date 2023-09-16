package com.example.digitaldiaryba.ui.components.card.media_object_card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.digitaldiaryba.util.enums.EMediaCardType
import com.example.digitaldiaryba.util.enums.EMediaType

@Composable
fun MediaObjectCard(
    cardType: EMediaCardType,
    mediaObjectType: EMediaType,
    mediaObjectUri: String,
    imagePlaceholderUri: String,
    onImagePlaceholderClick: () -> Unit,
    onVideoPlaceholderClick: () -> Unit,
    onAudioPlaceholderClick: (Boolean) -> Unit,
    playIcon: () -> Int,
    onTextClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
    ) {
    Card(
        border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.onSecondary)
    ) {
        Column {
            MediaObjectCardContent(
                cardType = cardType,
                onTextClick = onTextClick,
                onDeleteIconClick = onDeleteIconClick,
            )
            MediaObjectCardPreview(mediaObjectType = mediaObjectType, mediaObjectUri = mediaObjectUri, onImagePlaceholderClick = onImagePlaceholderClick, onVideoPlaceholderClick = onVideoPlaceholderClick, imagePlaceholderUri = imagePlaceholderUri, onAudioPlaceholderClick = onAudioPlaceholderClick, playIcon = playIcon)
        }
    }
}