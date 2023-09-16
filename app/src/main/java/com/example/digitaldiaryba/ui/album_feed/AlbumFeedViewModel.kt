package com.example.digitaldiaryba.ui.album_feed

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.data.models.AlbumObject
import com.example.digitaldiaryba.data.models.PresentationObject
import com.example.digitaldiaryba.data.repository.AlbumRepository
import com.example.digitaldiaryba.data.repository.MediaRepository
import com.example.digitaldiaryba.data.repository.PresentationRepository
import com.example.digitaldiaryba.ui.saved_presentations.concatPresentation
import com.example.digitaldiaryba.util.createSubtitleFile
import com.example.digitaldiaryba.util.encodeUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AlbumFeedViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val presentationRepository: PresentationRepository,
    private val mediaRepository: MediaRepository,
    private val context: Context
) : ViewModel() {
    private val _albumId = MutableStateFlow(0)

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private var albumList = _searchText.flatMapLatest { input ->
        if (input.isEmpty()) {
            albumRepository.getAllAlbums()
        } else {
            albumRepository.searchAlbums(input)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private var mediaList = _albumId.flatMapLatest { input ->
            if (input == 0) {
                mediaRepository.getAllMedias()
            } else {
                mediaRepository.getAllMediasFromAnAlbum(input)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var mediaNames = _albumId.flatMapLatest { input ->
        if (input == 0) {
            emptyFlow()
        } else {
            mediaRepository.getBuildingNamesForMedias(input)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var buildingInfos = _albumId.flatMapLatest { input ->
        if (input == 0) {
            emptyFlow()
        } else {
            mediaRepository.getBuildingInfosForMedias(input)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(AlbumFeedState())
    //_searchText
    val state = combine(_state, _albumId, albumList, mediaList, buildingInfos) { state, albumId, albums, medias, buildingInfos ->
        state.copy(
            medias = medias,
            albums = albums,
            albumId = albumId,
            //searchText = searchText,
            buildingInfos = buildingInfos
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AlbumFeedState())

    @RequiresApi(Build.VERSION_CODES.S)
    fun onEvent(event: AlbumFeedEvent) {
        when(event) {
            is AlbumFeedEvent.ChooseAlbumCoverPhotoUri -> {
                _state.update { it.copy(
                    albumCoverPhotoUri = event.coverPhotoUri
                ) }
            }
            is AlbumFeedEvent.ChooseAlbumDescription -> {
                _state.update { it.copy(
                    albumDescription = event.description
                ) }
            }
            is AlbumFeedEvent.ChooseAlbumName -> {
                _state.update { it.copy(
                    albumName = event.name
                ) }
            }
            AlbumFeedEvent.CreateAlbum -> {
                val albumName = state.value.albumName
                val albumDescription = state.value.albumDescription
                val albumCoverPhotoUri = state.value.albumCoverPhotoUri
                val album = AlbumObject(
                    albumId = 0,
                    name = albumName,
                    description = albumDescription,
                    coverPhotoUri = albumCoverPhotoUri
                )
                viewModelScope.launch {
                    albumRepository.insertAlbum(album)
                }
                _state.update { it.copy(
                    isCreatingAlbum = false,
                    albumName = "",
                    albumDescription = "",
                    albumCoverPhotoUri = ""
                ) }
            }
            is AlbumFeedEvent.DeleteAlbum -> {
                viewModelScope.launch {
                    albumRepository.deleteAlbum(event.album)
                }
            }
            AlbumFeedEvent.HideCreateAlbumDialog -> {
                _state.update { it.copy(
                    isCreatingAlbum = false
                ) }
            }
            AlbumFeedEvent.ShowCreateAlbumDialog -> {
                _state.update { it.copy(
                    isCreatingAlbum = true
                ) }
            }
            AlbumFeedEvent.ClickOnGoToPresentations -> {
                _state.update { it.copy(
                    isGoingToPresentations = true
                ) }
            }
            is AlbumFeedEvent.ChoosePresentationName -> {
                _state.update { it.copy(
                    presentationName = event.name
                ) }
            }
            AlbumFeedEvent.HideCreatePresentationDialog -> {
                _state.update { it.copy(
                    isCreatingPresentation = false
                ) }
            }
            is AlbumFeedEvent.ShowCreatePresentationDialog -> {
                _albumId.value = event.albumId
                _state.update { it.copy(
                    isCreatingPresentation = true,
                    albumId = event.albumId
                ) }
            }
            is AlbumFeedEvent.CreatePresentation -> {
                _state.update { it.copy(
                    isLoading = true
                ) }

                val presentationName = state.value.presentationName

                viewModelScope.launch {
                    val presentationUri = concatPresentation(state.value.medias, context)
                    val subtitlePath = createSubtitleFile(context, state.value.medias, mediaNames.value).absolutePath
                    _state.update { it.copy(
                        presentationUri = encodeUri(presentationUri),
                        presentationCoverPhotoUri = presentationUri.toString(),
                        subtitlePath = subtitlePath
                    ) }

                    state.value.buildingInfos.forEach {
                        Log.d(TAG, "state.value.buildingInfos.forEach: " + it.buildingInfoObjectName)
                        Log.d(TAG, "state.value.buildingInfos.forEach: " + it.location)
                    }

                    val presentation = PresentationObject(
                        presentationId = 0,
                        name = presentationName,
                        presentationUri = state.value.presentationUri,
                        coverPhotoUri = state.value.presentationCoverPhotoUri,
                        subtitlePath = subtitlePath,
                        buildingInfoList = state.value.buildingInfos
                    )
                    presentationRepository.insertPresentation(presentation)
                    _state.update { it.copy(
                        isCreatingPresentation = false,
                        presentationName = "",
                        presentationUri = "",
                        presentationCoverPhotoUri = "",
                        subtitlePath = ""
                    ) }

                    withContext(Dispatchers.Main) {
                        _state.update { it.copy(
                            isLoading = false
                        ) }
                    }
                }
            }
            is AlbumFeedEvent.Search -> {
                _searchText.value = event.searchText
                _state.update { it.copy(
                    searchText = event.searchText
                )
                }
            }
        }
    }

}