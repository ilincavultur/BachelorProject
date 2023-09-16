package com.example.digitaldiaryba.ui.saved_presentations

import com.example.digitaldiaryba.data.models.PresentationObject

data class PresentationFeedState(
    val presentations: List<PresentationObject> = emptyList(),
    val searchtext: String = "",
)