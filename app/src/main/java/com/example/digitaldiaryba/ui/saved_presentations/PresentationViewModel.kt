package com.example.digitaldiaryba.ui.saved_presentations

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.data.repository.PresentationRepository
import com.example.digitaldiaryba.ui.saved_presentations.video_playback.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PresentationViewModel @Inject constructor(
    private val presentationRepository: PresentationRepository
) : ViewModel() {
    private val _presentationId = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private var _buildingInfoList = _presentationId.flatMapLatest { input ->
        if (input == 0) {
            flow {  }
        } else {
            presentationRepository.getPresentationBuildingInfoList(input).map {
                it.buildingInfoList.map { buildingInfoObj ->
                    Log.d(TAG, "buildingInfoObj: " + buildingInfoObj.toPresentationListItem().title)
                    buildingInfoObj.toPresentationListItem()
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList<PresentationListItem>())

    @OptIn(ExperimentalCoroutinesApi::class)
    private var _videos = _presentationId.flatMapLatest { input ->
        if (input == 0) {
            flow {  }
        } else {
            presentationRepository.getPresentationBuildingInfoList(input).map {
                it.buildingInfoList.map { buildingInfoObj ->
                    buildingInfoObj.toVideoItem()
                }.filterNotNull()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList<VideoItem>())

    val currentlyPlayingIndex = MutableStateFlow<Int?>(null)

    private val _state = MutableStateFlow(MediaListState())
    val state = combine(_state, _presentationId, _buildingInfoList, _videos) { state, presentationId, buildingInfoList, videos ->
        state.copy(
            buildingInfos = buildingInfoList,
            presentationId = presentationId,
            videos = videos
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MediaListState())

    fun onEvent(event: PresentationEvent) {
        when(event) {
            is PresentationEvent.LandedOnScreen -> {
                Log.d(TAG, "onEvent: landed on screen " + event.presentationId)
                Log.d(TAG, "onEvent: landed on screen building infos")
                state.value.buildingInfos.forEach {
                    Log.d(TAG, "onEvent: landed on screen building infos " + it.title)
                }
                _presentationId.value = event.presentationId
                _state.update {
                    it.copy(
                        presentationId = event.presentationId,
                        buildingInfos = state.value.buildingInfos
                    )
                }
            }
        }
    }

    // START https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideosViewModel.kt
    fun onPlayVideoClick(playbackPosition: Long, videoIndex: Int) {
        Log.d(TAG, "onPlayVideoClick: currentlyPlayingIndex " +  videoIndex)
        when (currentlyPlayingIndex.value) {
            null -> currentlyPlayingIndex.value = videoIndex
            videoIndex -> {
                currentlyPlayingIndex.value = null
                state.value.videos = state.value.videos!!.toMutableList().also { list ->
                    list[videoIndex] = list[videoIndex].copy(lastPlayedPosition = playbackPosition)
                }
            }
            else -> {
                state.value.videos = state.value.videos!!.toMutableList().also { list ->
                    list[currentlyPlayingIndex.value!!] = list[currentlyPlayingIndex.value!!].copy(lastPlayedPosition = playbackPosition)
                }
                currentlyPlayingIndex.value = videoIndex
            }
        }
    }
    // END https://github.com/Skyyo/compose-video-playback/blob/master/app/src/main/java/com/skyyo/compose_video_playback/manualPlayback/VideosViewModel.kt
}