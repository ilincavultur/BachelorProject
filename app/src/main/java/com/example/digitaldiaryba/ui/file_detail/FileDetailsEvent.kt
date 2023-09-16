package com.example.digitaldiaryba.ui.file_detail

sealed interface FileDetailsEvent {
    object UpdateMedia: FileDetailsEvent
    data class LandedOnScreen(val mediaId: Int): FileDetailsEvent
    data class FetchLandmarkInfo(val refresh: Boolean): FileDetailsEvent
    object FetchDBpediaInfo: FileDetailsEvent
}