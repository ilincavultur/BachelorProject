package com.example.digitaldiaryba.ui.nearby_landmark

import android.content.Context
import android.location.Location
import com.example.digitaldiaryba.ui.file_detail.FileDetailsEvent
import com.example.digitaldiaryba.util.Geolocation

sealed interface NearbyLandmarkEvent {
    data class FetchNearbyLandmarks(val categories: String): NearbyLandmarkEvent
    object SaveFetchedLandmarksToDb: NearbyLandmarkEvent
    data class LandedOnScreen(val geolocation: Geolocation): NearbyLandmarkEvent
}