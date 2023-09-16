package com.example.digitaldiaryba.util

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import com.example.digitaldiaryba.util.enums.EMediaType
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// START https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec +
// documentation: https://developer.android.com/training/camerax
@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            mainExecutor
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView
): VideoCapture<Recorder> {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val qualitySelector = QualitySelector.from(
        Quality.FHD,
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )
    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        videoCapture,
    )

    return videoCapture
}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("MissingPermission")
fun startRecordingVideo(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    executor: Executor,
    videoFile: File,
    consumer: Consumer<VideoRecordEvent>,
): Recording {
    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    return videoCapture.output
        .prepareRecording(context, outputOptions)
        .start(executor, consumer)
}

@SuppressLint("UnsafeOptInUsageError")
@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createImageCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    context: Context
): ImageCapture {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val imageCapture = Builder()
        .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        imageCapture,
        preview,
    )

    return imageCapture
}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("MissingPermission")
fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    handleImageCapture: (Uri, String) -> Unit,
    executor: Executor
) {
    val imageFile = createFile(context, EMediaType.IMAGE)

    val outputOptions = OutputFileOptions.Builder(
        imageFile
    ).build()
    Log.d(TAG, "captureImage: new path " + imageFile.absolutePath)

    imageCapture.takePicture(outputOptions, executor,
        object : OnImageSavedCallback {
            override fun onError(error: ImageCaptureException)
            {
                Log.d(TAG, "onError: Image has not been saved")
                Log.d(TAG, "onError: " + error.toString())
            }
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d(TAG, "onImageSaved: Image has been saved " + outputFileResults.savedUri)

                outputFileResults.savedUri?.let {
                    handleImageCapture(it, imageFile.absolutePath)
                }
            }
        })
}
// END https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
// documentation: https://developer.android.com/training/camerax