package com.example.digitaldiaryba.ui.album_detail.add_video

import android.net.Uri
import com.example.digitaldiaryba.util.Geolocation

sealed interface AddVideoEvent {
    data class LandedOnScreen(val albumId: Int): AddVideoEvent
    data class SetFileDetails(val uri: Uri, val geolocation: Geolocation, val filepath: String): AddVideoEvent
    object InsertVideoIntoDb: AddVideoEvent
}