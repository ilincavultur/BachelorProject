package com.example.digitaldiaryba.ui.album_feed

import android.content.Context
import com.example.digitaldiaryba.data.models.AlbumObject
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import com.example.digitaldiaryba.data.models.MediaObject

data class AlbumFeedState(
    val albums: List<AlbumObject> = emptyList(),
    val medias: List<MediaObject> = emptyList(),
    val mediaNames: List<String> = emptyList(),
    val buildingInfos: List<BuildingInfoObject> = emptyList(),
    val albumName: String = "",
    val albumDescription: String = "",
    val albumCoverPhotoUri: String = "",
    val isCreatingAlbum: Boolean = false,
    val isCreatingPresentation: Boolean = false,
    val albumId: Int = 0,
    val isGoingToPresentations: Boolean = false,
    val presentationName: String = "",
    val presentationUri: String = "",
    val presentationCoverPhotoUri: String = "",
    val subtitlePath: String = "",
    val searchText: String = "",
    val isLoading: Boolean = false
)
