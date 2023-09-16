package com.example.digitaldiaryba.ui.file_detail

sealed class FileDetailsUiEvent {
    data class ShowSnackbar(val message: String): FileDetailsUiEvent()
}
