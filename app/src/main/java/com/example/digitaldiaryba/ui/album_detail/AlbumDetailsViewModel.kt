package com.example.digitaldiaryba.ui.album_detail

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.data.repository.MediaRepository
import com.example.digitaldiaryba.ui.album_detail.add_audio.AudioFilename
import com.example.digitaldiaryba.util.enums.EMediaType
import com.example.digitaldiaryba.util.encodeUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val _albumId = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private var mediaList = _albumId.flatMapLatest { input ->
        if (input == 0) {
            mediaRepository.getAllMedias()
        } else {
            mediaRepository.getAllMediasFromAnAlbum(input)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(AlbumDetailsState())
    val state = combine(_state, _albumId, mediaList) { state, albumId, medias ->
        state.copy(
            medias = medias,
            albumId = albumId,
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AlbumDetailsState())

    private val _fileDetailState = MutableStateFlow(FileDetailState())
    val fileDetailState = combine(_fileDetailState, _albumId) { state, albumId ->
        state.copy(
            albumId = albumId,
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), FileDetailState())

    private val _fileMetadataState = MutableStateFlow(AudioFilename())
    val fileMetadataState = _fileMetadataState

    fun onEvent(event: AlbumDetailsEvent) {
        when(event) {
            is AlbumDetailsEvent.DeleteMediaObject -> {
                viewModelScope.launch {
                    mediaRepository.deleteMedia(event.mediaObject)

                    // also from firebase storage - if it's an image
                    if (event.mediaObject.type == EMediaType.IMAGE) {
                        val storageReference = Firebase.storage.reference
                        val file = Uri.fromFile(File(event.filepath))

                        val fileReference = storageReference.child("${file.lastPathSegment}")


                        fileReference.delete().addOnSuccessListener {
                            Log.d(TAG, "onEvent: DeleteFileFromCloudStorage ")
                        }.addOnFailureListener {
                            Log.d(TAG, "onEvent: DeleteFileFromCloudStorage error")
                        }
                    }

                }
            }
            is AlbumDetailsEvent.LandedOnScreen -> {
                _albumId.value = event.albumId
            }
            AlbumDetailsEvent.HideRecordAudioDialog -> {
                _state.update { it.copy(
                    isRecordingAudio = false
                ) }
            }
            is AlbumDetailsEvent.ShowRecordAudioDialog -> {
                _state.update { it.copy(
                    isRecordingAudio = true
                ) }
            }
            is AlbumDetailsEvent.ExtractMetadata -> {
                val newFilename = event.uri.path?.substringAfterLast("/")

                _fileMetadataState.update { it.copy(
                    filename = newFilename
                ) }
            }
            AlbumDetailsEvent.InsertAudioIntoDb -> {
                viewModelScope.launch {
                    if (_fileDetailState.value.isValid()) {
                        mediaRepository.insertMediaAndCorrespondingAudioObject(
                            _fileDetailState.value.toMediaObject(),
                        )
                    }
                }
                _state.update { it.copy(
                    isRecordingAudio = false
                ) }
            }
            is AlbumDetailsEvent.SetFileDetails -> {
                _fileDetailState.update { it.copy(
                    albumId = state.value.albumId,
                    type = EMediaType.AUDIO,
                    name = _fileMetadataState.value.filename,
                    locationString = event.geolocation.latitude.toString() + " " + event.geolocation.latitude.toString(),
                    description = "",
                    decodedUri = event.uri,
                    uri = encodeUri(event.uri.toUri())
                ) }
            }
        }
    }
}