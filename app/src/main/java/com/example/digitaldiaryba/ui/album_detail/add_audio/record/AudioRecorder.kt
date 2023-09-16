package com.example.digitaldiaryba.ui.album_detail.add_audio.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import com.example.digitaldiaryba.util.enums.EMediaType
import com.example.digitaldiaryba.util.createFile

import java.io.File

// START https://developer.android.com/guide/topics/media/mediarecorder
class AudioRecorder(
    context: Context
) : IAudioRecorder {

    private var recorder: MediaRecorder? = null
    private var fileName: String = ""
    var mediaDir = File("")
    var filenameFormat = ""
    var outputDirectory = File("")
    var audioFile = mutableStateOf(File(""))

    @RequiresApi(Build.VERSION_CODES.S)
    override fun startRecording(context: Context) {
        audioFile.value = createFile(context, EMediaType.AUDIO)

        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFile.value)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
    }

    override fun stopRecording() : String {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
        return audioFile.value.path
    }
}
// END https://developer.android.com/guide/topics/media/mediarecorder