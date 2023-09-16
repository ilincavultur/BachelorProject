package com.example.digitaldiaryba.ui.album_detail.add_audio.play

import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// START https://developer.android.com/guide/topics/media/mediarecorder
class AudioPlayer(
    var isPlaying : MutableState<Boolean>
) : IAudioPlayer{

    private var player: MediaPlayer? = null

    override fun playRecording(filename: String) {
        isPlaying.value = true
        player = MediaPlayer().apply {
            setDataSource(filename)
            prepare()
            start()
        }
    }

    override fun stopPlaying() {
        isPlaying.value = false
        player?.release()
        player = null
    }

}
// END https://developer.android.com/guide/topics/media/mediarecorder