package com.example.digitaldiaryba.ui.components.card.media_object_card

import androidx.compose.foundation.layout.*

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.enums.EMediaCardType

@Composable
fun MediaObjectCardContent(
    cardType: EMediaCardType,
    onTextClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
) {
    Surface(
        modifier = when(cardType) {
            EMediaCardType.ALBUM_FEED -> Modifier
                .size(Constants.FEED_CARD_CONTENT_WIDTH, Constants.FEED_CARD_CONTENT_HEIGHT)
            EMediaCardType.DETAILS -> Modifier
                .size(Constants.DETAILS_CARD_CONTENT_WIDTH, Constants.DETAILS_CARD_CONTENT_HEIGHT) }

    ) {
        NoDetailsCardContent(onTextClick = onTextClick, onDeleteIconClick = onDeleteIconClick)
    }
}