package com.example.digitaldiaryba.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.DefaultCameraDistance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.file_detail.FileDetailsUiEvent

import com.example.digitaldiaryba.ui.theme.AccentPurple

import com.example.digitaldiaryba.util.startRecordingVideo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.collectLatest


@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    latitude: String,
    longitude: String,
    landmarkName: String,
    viewModel: MapViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val path = viewModel.path.observeAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(snackbarHostState) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is MapUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    val zoom = rememberSaveable {
        mutableStateOf(15f)
    }

    val targetLng = longitude.toDouble() ?: 0.0
    val targetLat = latitude.toDouble() ?: 0.0
    val targetPosition = LatLng(targetLat, targetLng)

    // Location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val curLongitude = rememberSaveable {
        mutableStateOf(0.0)
    }

    val curLatitude = rememberSaveable {
        mutableStateOf(0.0)
    }

    // START https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }
        return
    } // END https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
    fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
        curLongitude.value = location?.longitude ?: 0.0
        curLatitude.value = location?.latitude ?: 0.0
    }

    val targetString = "$latitude,$longitude"
    val sourceString = "${curLatitude.value.toString()},${curLongitude.value.toString()}"

    val drawPolylines = mutableListOf<LatLng>()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetPosition, zoom.value)
    }

    val mapProperties by remember { mutableStateOf(MapProperties(
        isMyLocationEnabled = true,
    )) }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            Log.d(TAG, "Map camera started moving due to ${cameraPositionState.cameraMoveStartedReason.name}")
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
        ) {
            Marker(
                state = MarkerState(position = targetPosition),
                title = "$landmarkName",
                snippet = "target"
            )

            path.value?.forEach {
                it.forEach { latLng ->
                    drawPolylines += latLng
                }
            }
            Polyline(
                points = drawPolylines,
                color = Magenta,
                width = 10f
            )
        }

        if (state.isLoading) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(70.dp)
            ,
            backgroundColor = AccentPurple,
           contentColor = White ,
            elevation = FloatingActionButtonDefaults.elevation(10.dp),
            onClick = {
            viewModel.onEvent(MapEvent.ComputeRoute(sourceString, targetString))
        },
        ) {
            Text(text = "Route")
        }

    }
}