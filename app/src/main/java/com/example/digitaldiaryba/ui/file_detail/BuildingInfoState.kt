package com.example.digitaldiaryba.ui.file_detail

import com.example.digitaldiaryba.data.models.BuildingInfoObject
import com.example.digitaldiaryba.util.enums.EMediaType

// https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#10
data class BuildingInfoUiState(
    val buildingInfoId: Int = 0,
    val mediaId: Int = 0,
    val architect: String = "",
    //val country: String = "",
    val description: String = "",
    val location: String = "",
    val longitude: String = "",
    val latitude: String = "",
    val name: String = "",
    val filename: String = "",
    val year: String = "",
    val confidenceScore: String = "",
    val fileUri: String = "",
    val mediaType: EMediaType = EMediaType.IMAGE,
)

fun BuildingInfoUiState.toBuildingInfoObject(): BuildingInfoObject = BuildingInfoObject(
    buildingInfoId = null,
    mediaId = mediaId,
    architect = architect,
    country = "",
    location = location,
    description = description,
    year = year,
    buildingInfoObjectName = name,
    filename = filename,
    longitude = longitude,
    latitude = latitude,
    confidenceScore = confidenceScore,
    fileUri = fileUri,
    mediaType = mediaType
)
