package com.example.digitaldiaryba.ui.saved_presentations

import com.example.digitaldiaryba.ui.saved_presentations.video_playback.VideoItem

data class MediaListState(
    val presentationId: Int = 0,
    val buildingInfos: List<PresentationListItem> = emptyList(),
    var videos: List<VideoItem> = emptyList(),
)
