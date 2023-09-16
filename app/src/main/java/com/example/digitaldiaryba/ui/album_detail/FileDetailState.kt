package com.example.digitaldiaryba.ui.album_detail

import android.content.ContentValues.TAG
import android.util.Log
import com.example.digitaldiaryba.data.models.*
import com.example.digitaldiaryba.util.enums.EMediaType
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

// https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#10
data class FileDetailState(
    var albumId: Int = 0,
    var type: EMediaType = EMediaType.IMAGE,
    var name: String? = "",
    var description: String? = "",
    var date: String? = SimpleDateFormat("yyyy.MM.dd").format(Date.from(Instant.now())),
    var time: String? = SimpleDateFormat("HH:mm").format(Date.from(Instant.now())),
    var locationString: String? = "",
    var decodedUri: String = "",
    var uri: String = "",
    var gsUri: String = "",
    var filepath: String = "",
)

fun FileDetailState.toMediaObject(): MediaObject = MediaObject(
    mediaId = null,
    albumId = albumId,
    type = type,
    name = name,
    description = description,
    date = date,
    time = time,
    locationString = locationString,
    decodedUri = decodedUri,
    uri = uri,
    gsUri = gsUri,
    filepath = filepath
)

fun FileDetailState.isValid() : Boolean {

    if (uri.isEmpty()) {
        Log.d(TAG, "isValid: ?: uri is empty")
    }

    if (decodedUri.isEmpty()) {
        Log.d(TAG, "isValid: ?: decodedUri is empty")
    }

    if (albumId == 0) {
        Log.d(TAG, "isValid: ?: albumId is 0")
    }
    return uri.isNotBlank() && decodedUri.isNotBlank() && albumId != 0

}