package com.example.digitaldiaryba.ui.nearby_landmark

import com.example.digitaldiaryba.util.Geolocation

data class NearbyLandmarkState(
    val nearby_landmark_list: List<String> = emptyList(),
    //val nearby_landmark_list_cached: List<String> = emptyList(),
    val nearby_landmark_list_type: String = "",
    var isLoading: Boolean = false,
    var userCurLocation: Geolocation = Geolocation(),
)