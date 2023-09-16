package com.example.digitaldiaryba.ui.saved_presentations

import com.example.digitaldiaryba.util.enums.EMediaType

data class PresentationListItem(
    val title: String? = "Eiffel Tower",
    val architect: String? = "Architect",
    val location: String? = "Location",
    val year: String? = "Year built",
    val imgUri: String? = "",
    val type: EMediaType = EMediaType.IMAGE
)
