package com.example.digitaldiaryba.ui.album_detail

import com.example.digitaldiaryba.data.models.MediaObject

data class AlbumDetailsState(
    val medias: List<MediaObject> = emptyList(),
    val albumId: Int = 0,
    val isRecordingAudio: Boolean = false,
)
