package com.example.digitaldiaryba.ui.nearby_landmark


sealed class NearbyLandmarkUiEvent {
    data class ShowSnackbar(val message: String): NearbyLandmarkUiEvent()
}
