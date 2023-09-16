package com.example.digitaldiaryba.ui.saved_presentations

import com.example.digitaldiaryba.data.models.PresentationObject

sealed interface PresentationFeedEvent {
    data class DeletePresentation(val presentation: PresentationObject): PresentationFeedEvent
    data class Search(val searchText: String): PresentationFeedEvent
}