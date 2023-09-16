package com.example.digitaldiaryba.ui.album_detail.add_image

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.data.repository.MediaRepository
import com.example.digitaldiaryba.ui.album_detail.FileDetailState
import com.example.digitaldiaryba.ui.album_detail.isValid
import com.example.digitaldiaryba.ui.album_detail.toMediaObject
import com.example.digitaldiaryba.ui.file_detail.BuildingInfoUiState
import com.example.digitaldiaryba.ui.file_detail.toBuildingInfoObject
import com.example.digitaldiaryba.util.enums.EMediaType
import com.example.digitaldiaryba.util.encodeUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddImageViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _albumId = MutableStateFlow(0)

    private val _fileDetailState = MutableStateFlow(FileDetailState())
    val fileDetailState = combine(_fileDetailState, _albumId) { state, albumId ->
        state.copy(
            albumId = albumId,
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), FileDetailState())

    fun onEvent(event: AddImageEvent) {
        when(event) {
            is AddImageEvent.LandedOnScreen -> {
                _albumId.value = event.albumId
                _fileDetailState.update { it.copy(
                    albumId = event.albumId
                ) }
            }
            is AddImageEvent.SetFileDetails -> {
                _fileDetailState.update { it.copy(
                    albumId = _fileDetailState.value.albumId,
                    type = EMediaType.IMAGE,
                    name = event.filepath.substringAfterLast("/"),
                    locationString = event.geolocation.latitude.toString() + " " + event.geolocation.latitude.toString(),
                    description = event.description ?: "",
                    decodedUri = event.uri.toString(),
                    uri = encodeUri(event.uri),
                    gsUri = "",
                    filepath = event.filepath
                ) }
            }
            is AddImageEvent.InsertImageIntoDb -> {
                val storageReference = Firebase.storage.reference;
                var file = Uri.fromFile(File(event.filepath))
                Log.d(ContentValues.TAG, "handleImageCapture: filepath " + file.lastPathSegment)
                val fileReference = storageReference.child("${file.lastPathSegment}")
                val uploadTask = fileReference.putFile(file)
                Log.d(ContentValues.TAG, "onEvent: fileRef" + Firebase.storage.getReferenceFromUrl(fileReference.toString()))

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val downloadUri = task.result
                        Log.d(ContentValues.TAG, "onEvent:task.isSuccessful + " + downloadUri)
                        return@addOnCompleteListener
                    } else {
                        Log.d(ContentValues.TAG, "onEvent:task.notSuccessful + ")
                    }
                }

                viewModelScope.launch {
                    if (_fileDetailState.value.isValid()) {
                        mediaRepository.insertImageAndCorrespondingBuildingInfoState(
                            _fileDetailState.value.toMediaObject(),
                            BuildingInfoUiState(0).toBuildingInfoObject()
                        )
                    }
                }
            }
        }
    }
}