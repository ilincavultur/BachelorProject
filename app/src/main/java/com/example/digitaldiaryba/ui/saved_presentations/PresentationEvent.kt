package com.example.digitaldiaryba.ui.saved_presentations

sealed interface PresentationEvent {
    data class LandedOnScreen(val presentationId: Int) : PresentationEvent

}