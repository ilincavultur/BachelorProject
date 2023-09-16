package com.example.digitaldiaryba.ui.nearby_landmark


data class NearbyLandmarkListState(
    var nearbyLandmarkList: List<String> = emptyList(),
)

data class NearbyLandmarkListTypeState(
    var nearbyLandmarkListType: String = "",
)