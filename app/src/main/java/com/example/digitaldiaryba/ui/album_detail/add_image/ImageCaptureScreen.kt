package com.example.digitaldiaryba.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.album_detail.add_image.AddImageEvent
import com.example.digitaldiaryba.ui.album_detail.add_image.AddImageViewModel
import com.example.digitaldiaryba.ui.file_detail.FileDetailsEvent

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

// https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageCaptureScreen(
    navController: NavController,
    addImageViewModel: AddImageViewModel = hiltViewModel(),
    albumId: Int?
) {
    val fileDetailState by addImageViewModel.fileDetailState.collectAsState()

    addImageViewModel.onEvent(AddImageEvent.LandedOnScreen(albumId!!))

    fun handleImageCapture(uri: Uri, filepath: String) {
        Log.d(TAG, "handleImageCapture: filepath " + filepath)
        addImageViewModel.onEvent(AddImageEvent.SetFileDetails(uri, Geolocation(), description = "", filepath = filepath))
        addImageViewModel.onEvent(AddImageEvent.InsertImageIntoDb(filepath))
        navController.popBackStack()
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // START https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        )
    )

    // Image Capture
    val previewView: PreviewView = remember { PreviewView(context) }
    val imageCapture: MutableState<ImageCapture?> = remember { mutableStateOf(null) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(
            CameraSelector.DEFAULT_BACK_CAMERA
        )
    }

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(previewView) {
        imageCapture.value = context.createImageCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView,
            context = context,
        )
    }
    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = { /* ... */ },
        permissionsNotAvailableContent = { /* ... */ }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = {
                    imageCapture.value?.let { imageCapture ->
                        captureImage(
                            context = context,
                            imageCapture = imageCapture,
                            executor = context.mainExecutor,
                            handleImageCapture = ::handleImageCapture,
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_record),
                    contentDescription = "",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colors.onSecondary
                )
            }
        }
    }
    // END https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
}




