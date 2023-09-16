package com.example.digitaldiaryba.ui.album_detail.add_video

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddVideoViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _albumId = MutableStateFlow(0)

    private val _fileDetailState = MutableStateFlow(FileDetailState())
    val fileDetailState = combine(_fileDetailState, _albumId) { state, albumId ->
        state.copy(
            albumId = albumId,
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), FileDetailState())

    fun onEvent(event: AddVideoEvent) {
        when(event) {
            is AddVideoEvent.InsertVideoIntoDb -> {
                viewModelScope.launch {
                    if (_fileDetailState.value.isValid()) {
                        mediaRepository.insertVideoAndCorrespondingBuildingInfoState(
                            _fileDetailState.value.toMediaObject(),
                            BuildingInfoUiState(0).toBuildingInfoObject()
                        )
                    }
                }
            }
            is AddVideoEvent.LandedOnScreen -> {
                _albumId.value = event.albumId
                _fileDetailState.update { it.copy(
                    albumId = event.albumId
                ) }
            }
            is AddVideoEvent.SetFileDetails -> {
                _fileDetailState.update { it.copy(
                    albumId = _fileDetailState.value.albumId,
                    type = EMediaType.VIDEO,
                    name = event.filepath.substringAfterLast("/"),
                    locationString = event.geolocation.latitude.toString() + " " + event.geolocation.latitude.toString(),
                    description = "",
                    decodedUri = event.uri.toString(),
                    uri = encodeUri(event.uri),
                    filepath = event.filepath
                ) }
            }
        }
    }
}