package com.example.digitaldiaryba.ui.album_feed

import com.example.digitaldiaryba.data.models.AlbumObject

sealed interface AlbumFeedEvent {
    object CreateAlbum: AlbumFeedEvent
    data class ChooseAlbumName(val name: String): AlbumFeedEvent
    data class ChooseAlbumDescription(val description: String): AlbumFeedEvent
    data class ChooseAlbumCoverPhotoUri(val coverPhotoUri: String): AlbumFeedEvent
    object ShowCreateAlbumDialog: AlbumFeedEvent
    object HideCreateAlbumDialog: AlbumFeedEvent
    data class DeleteAlbum(val album: AlbumObject): AlbumFeedEvent
    object CreatePresentation: AlbumFeedEvent
    data class ShowCreatePresentationDialog(val albumId: Int): AlbumFeedEvent
    object HideCreatePresentationDialog: AlbumFeedEvent
    object ClickOnGoToPresentations: AlbumFeedEvent
    data class ChoosePresentationName(val name: String): AlbumFeedEvent
    data class Search(val searchText: String): AlbumFeedEvent
}