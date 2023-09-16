package com.example.digitaldiaryba.ui.album_detail.add_image

data class AddImageState(
    val isLoading: Boolean = false,
    val onError: Boolean = false,
    val isAddedToCloudStorage: Boolean = false,
)
