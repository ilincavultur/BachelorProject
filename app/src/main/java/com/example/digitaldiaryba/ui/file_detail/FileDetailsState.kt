package com.example.digitaldiaryba.ui.file_detail

import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.util.enums.EMediaType

data class FileDetailsState(
    val albumId: Int = 0,
    val uri: String = "",
    val mediaId: Int = 0,
    val buildingInfoId: Int = 0,
    var filepath: String = "",
    val mediaObject: MediaObject = MediaObject(null, 0, EMediaType.IMAGE, "", "", "", "", "", "", "", gsUri = "", filepath = ""),
    val gsUri: String = "",
    val refreshAccessToken: String = "",
    val landmarkinfoButtonIsEnabled: Boolean = false,
    val dbpediaButtonIsEnabled: Boolean = false,
    var isLoading: Boolean = false,
    var onError: Boolean = false,
    var resultsNotFound: Boolean = false,
)
