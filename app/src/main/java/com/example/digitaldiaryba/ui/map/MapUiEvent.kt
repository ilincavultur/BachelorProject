package com.example.digitaldiaryba.ui.map

sealed class MapUiEvent {
    data class ShowSnackbar(val message: String): MapUiEvent()
}