package com.example.digitaldiaryba.ui.saved_presentations.video_playback

data class VideoItem(
    var id: Int,
    val mediaUrl: String,
    val thumbnail: String,
    val lastPlayedPosition: Long = 0
)