package com.example.digitaldiaryba.ui.album_detail.add_audio.record

import android.content.Context
import java.io.File

interface IAudioRecorder {
    fun startRecording(context: Context)
    fun stopRecording() : String
}