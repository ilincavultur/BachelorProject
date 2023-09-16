package com.example.digitaldiaryba.ui.album_detail

import android.net.Uri
import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.ui.file_detail.FileDetailsEvent

import com.example.digitaldiaryba.util.Geolocation

sealed interface AlbumDetailsEvent {
    data class DeleteMediaObject(val mediaObject: MediaObject, val filepath: String): AlbumDetailsEvent
    data class LandedOnScreen(val albumId: Int): AlbumDetailsEvent
    data class ShowRecordAudioDialog(val albumId: Int): AlbumDetailsEvent
    object HideRecordAudioDialog: AlbumDetailsEvent
    data class SetFileDetails(val uri: String, val geolocation: Geolocation): AlbumDetailsEvent
    data class ExtractMetadata(val uri: Uri): AlbumDetailsEvent
    object InsertAudioIntoDb: AlbumDetailsEvent
}