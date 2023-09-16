package com.example.digitaldiaryba.ui.album_detail.add_image

import android.net.Uri
import com.example.digitaldiaryba.util.Geolocation

sealed interface AddImageEvent {
    data class LandedOnScreen(val albumId: Int): AddImageEvent
    data class SetFileDetails(val uri: Uri, val geolocation: Geolocation, val description: String?, val filepath: String): AddImageEvent
    data class InsertImageIntoDb(val filepath: String): AddImageEvent
}