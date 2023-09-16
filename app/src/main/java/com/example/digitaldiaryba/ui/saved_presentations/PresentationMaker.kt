package com.example.digitaldiaryba.ui.saved_presentations

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.util.enums.EMediaType
import com.example.digitaldiaryba.util.createFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.S)
suspend fun concatPresentation(mediaObjects: List<MediaObject>, context: Context ) : Uri = withContext(Dispatchers.Default) {
    val imageSlideshow = withContext(Dispatchers.Default) {
        concatImages(
            mediaObjects,
            context
        )
    }
    val videoSlideshow = withContext(Dispatchers.Default) {
        concatVideos(
            mediaObjects,
            context
        )
    }
    return@withContext withContext(Dispatchers.Default) {
        concatFinalVideos(
            imageSlideshow.path,
            videoSlideshow.path,
            context
        )
    }
}

// returns the path of a video slideshow with all images
suspend fun concatImages(imageObjects: List<MediaObject>, context: Context) : Uri = withContext(Dispatchers.Default) {
    var mediaNumber = 0
    val newFile = createFile(context = context, EMediaType.VIDEO)
    val inputDirectory = "/storage/emulated/0/Android/data/com.example.digitaldiaryba/cache/DigitalDiaryBA/"
    val timeInSeconds = 5
    val framerate = "-framerate 1/${timeInSeconds} "
    var files = ""
    var file = ""
    val filterComplex = "-filter_complex "
    var filterComplexArgs = "\""
    var filterComplexArgsText = ""
    val map = "-map \"[v]\" "
    val scale = "-s 1080x1920 "
    var commandString = ""
    imageObjects.forEach {
        if (it.type == EMediaType.IMAGE) {
            files += framerate + "-i ${inputDirectory}${it.name} "
            file += "-i ${inputDirectory}${it.name} "
            filterComplexArgs += "[${mediaNumber}:v] "
            mediaNumber++
        }
    }
    if (mediaNumber == 0) {
        return@withContext Uri.EMPTY
    }
    if (mediaNumber != 1) {
        filterComplexArgs += "concat=n=${mediaNumber}:v=1:a=0, transpose=1 [v]\" "

        commandString += files + filterComplex + filterComplexArgs + map + scale + "-r 1 -vsync 2 " + newFile.absolutePath
    } else {
        Log.d(TAG, "concatImages: single image file")
        commandString += "-loop 1 -framerate 1/10 " + file + scale + "-t 10 -vf transpose=1 " + newFile.absolutePath
    }

    Log.d(TAG, "concatImages: commandString " + commandString)

    val rc = FFmpeg.execute(commandString);

    if (rc == RETURN_CODE_SUCCESS) {
        Log.d(TAG, "createPresentation: success ffmpeg concatImages")
    } else if (rc == RETURN_CODE_CANCEL) {
        Log.i(Config.TAG, "Command execution cancelled by user. concatImages");
    } else {
        Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below. concatImages", rc));
        Config.printLastCommandOutput(Log.INFO);
    }
    newFile.absolutePath.toUri()
}

// returns the path of a video slideshow with all videos
suspend fun concatVideos(videoObjects: List<MediaObject>, context: Context) : Uri = withContext(Dispatchers.Default) {
    var mediaNumber = 0
    val newFile = createFile(context = context, EMediaType.VIDEO)
    val inputDirectory = "/storage/emulated/0/Android/data/com.example.digitaldiaryba/cache/DigitalDiaryBA/"

    var files = ""
    var fileUri = ""
    val filterComplex = "-filter_complex \""
    var filterComplexArgs = ""
    val map = "-map \"[v]\" "

    var commandString = ""
    videoObjects.forEach {
        if (it.type == EMediaType.VIDEO) {
            files += "-i ${inputDirectory}${it.name} "
            fileUri = inputDirectory + it.name
            Log.d(TAG, "createPresentation: concatVideos single video file uri" + it.decodedUri)
            filterComplexArgs += "[${mediaNumber}:v] "
            mediaNumber++
        }
    }
    if (mediaNumber == 0) {
        return@withContext Uri.EMPTY
    }
    if (mediaNumber == 1) {
        return@withContext fileUri.toUri()
    }
    filterComplexArgs += "concat=n=${mediaNumber}:v=1 [v] \" "
    commandString += files + filterComplex + filterComplexArgs + map + "-r 24 -vsync 2 " + newFile.absolutePath
    Log.d(TAG, "concatVideos: commandString " + commandString)


    val rc = FFmpeg.execute(commandString);

    if (rc == RETURN_CODE_SUCCESS) {
        Log.d(TAG, "createPresentation: success ffmpeg concatVideos")
    } else if (rc == RETURN_CODE_CANCEL) {
        Log.i(Config.TAG, "Command execution cancelled by user. concatVideos");
    } else {
        Log.i(
            Config.TAG,
            String.format("Command execution failed with rc=%d and the output below. concatVideos", rc)
        );
        Config.printLastCommandOutput(Log.INFO);
    }

    newFile.absolutePath.toUri()
}

suspend fun concatFinalVideos(firstPath: String?, secondPath: String?, context: Context) : Uri = withContext(Dispatchers.Default) {
    val newFile = createFile(context = context, EMediaType.VIDEO)

    Log.d(TAG, "concatFinalVideos: first path: " + firstPath)
    Log.d(TAG, "concatFinalVideos: second path: " + secondPath)
    if (firstPath?.equals(Uri.EMPTY.toString()) == true && secondPath?.equals(Uri.EMPTY.toString()) == false) {
        Log.d(TAG, "concatFinalVideos: first path is empty, second not, so only videos")
        return@withContext secondPath.toUri()
    }

    if (secondPath?.equals(Uri.EMPTY.toString()) == true && firstPath?.equals(Uri.EMPTY.toString()) == false) {
        Log.d(TAG, "concatFinalVideos: second path is empty, first not so only images")
        return@withContext firstPath.toUri()
    }

    val commandString = "-i ${firstPath} -i ${secondPath} -filter_complex \"[0:v]scale=1080x1920,setdar=1080/1920[a]; [1:v]scale=1080x1920,setdar=1080/1920[b]; [a][b]concat=n=2:v=1 [v] \" -map \"[v]\" -r 24 -vsync 2 " + newFile.absolutePath

    Log.d(TAG, "concatFinalVideos: comandString " + commandString)
    val rc = FFmpeg.execute(commandString);

    if (rc == RETURN_CODE_SUCCESS) {
        Log.d(TAG, "createPresentation: success ffmpeg concatFinalVideos")
    } else if (rc == RETURN_CODE_CANCEL) {
        Log.i(Config.TAG, "Command execution cancelled by user. concatFinalVideos");
    } else {
        Log.i(
            Config.TAG,
            String.format("Command execution failed with rc=%d and the output below. concatFinalVideos", rc)
        );
        Config.printLastCommandOutput(Log.INFO);
    }
    newFile.absolutePath.toUri()
}