package com.example.digitaldiaryba.ui.album_detail.add_audio.play

interface IAudioPlayer {
    fun playRecording(filename: String)
    fun stopPlaying()
}