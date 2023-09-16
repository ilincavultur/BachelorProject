package com.example.digitaldiaryba.util

import android.content.ContentValues.TAG
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.util.enums.EMediaType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*


// START https://stackoverflow.com/questions/72448799/how-to-capture-and-display-a-picture-with-camera-in-jetpack-compose
fun createFile(context: Context, type: EMediaType): File {
    val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
        File(it, context.getString(com.example.digitaldiaryba.R.string.app_name)).apply { mkdirs() }
    }
    val outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    Log.d(TAG, "createFile: outputdirectory " + outputDirectory.path)
    val suffix = when(type) {
        EMediaType.IMAGE -> ".jpg"
        EMediaType.VIDEO -> ".mp4"
        EMediaType.AUDIO -> ".mp3"
    }
    Log.d(TAG, "createFile: suffix " + suffix)

    return File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + suffix
    )
}

fun createSubtitleFile(context: Context, mediaObjects: List<MediaObject>, names: List<String>) : File {

    names.forEach {
        Log.d(TAG, "createSubtitleFile: name " + it.toString())
    }

    val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
        File(it, context.getString(com.example.digitaldiaryba.R.string.app_name)).apply { mkdirs() }
    }
    val suffix = ".srt"
    val outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir

    val srtFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + suffix
    )
    val outputStream = FileOutputStream(srtFile)
    val outputStreamWriter = OutputStreamWriter(outputStream)

    var number = 0
    var seconds = 0

    var mediaNo = 1
    var mediaIdx = 0
    var duration: Int
    mediaObjects.forEach {

        if (it.type == EMediaType.IMAGE) {
            if (mediaObjects.size == 1) {
                duration = 1500
            } else {
                duration = 900
            }

            var startTimestamp = "00:00:0$seconds,$number"
            number += duration
            while (number >= 1000) {
                number -= 1000
                seconds += 1
            }
            var endTimestamp = "00:00:0$seconds,$number"

            outputStreamWriter.write("${mediaNo}\n")
            outputStreamWriter.write("$startTimestamp --> $endTimestamp\n")

            Log.d(TAG, "createSubtitleFile: mediaIdx " + mediaIdx)

            if (!names[mediaIdx].isNullOrEmpty()) {
                outputStreamWriter.write("${names[mediaIdx]}\n\n")
            } else {
                outputStreamWriter.write("${it.name}\n\n")
            }
            mediaIdx++
            mediaNo++

            Log.d(TAG, "createSubtitleFile: startimestamp: " + startTimestamp)
            Log.d(TAG, "createSubtitleFile: endTimestamp: " + endTimestamp)

        }

    }


    mediaObjects.forEach {

        if (it.type == EMediaType.VIDEO) {
            //https://stackoverflow.com/questions/3936396/how-to-get-duration-of-a-video-file
            val mp: MediaPlayer = MediaPlayer.create(context, Uri.parse(it.decodedUri))
            duration = mp.duration
            mp.release()
            Log.d(TAG, "createSubtitleFile: duration of video " + duration)

            var startTimestamp = "00:00:0$seconds,$number"
            number += duration
            while (number >= 1000) {
                number -= 1000
                seconds += 1
            }
            var endTimestamp = "00:00:0$seconds,$number"

            outputStreamWriter.write("${mediaNo}\n")
            outputStreamWriter.write("$startTimestamp --> $endTimestamp\n")

            Log.d(TAG, "createSubtitleFile: mediaIdx " + mediaIdx)

            outputStreamWriter.write("${it.name}\n\n")

            mediaNo++

            Log.d(TAG, "createSubtitleFile: startimestamp: " + startTimestamp)
            Log.d(TAG, "createSubtitleFile: endTimestamp: " + endTimestamp)

        }
    }

    Log.d(TAG, "createSubtitleFile: srt file path " + srtFile.absolutePath)

    outputStreamWriter.close()
    outputStream.close()

    return srtFile
}
// END https://stackoverflow.com/questions/72448799/how-to-capture-and-display-a-picture-with-camera-in-jetpack-compose