package com.example.digitaldiaryba.ui.nearby_landmark

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.navigation.Screen
import com.example.digitaldiaryba.ui.components.dropdown_menu.DropdownMenuFoursquareApi
import com.example.digitaldiaryba.ui.components.text.ExpandingText
import com.example.digitaldiaryba.ui.map.MapUiEvent
import com.example.digitaldiaryba.util.Geolocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NearbyLandmarkScreen(
    navController: NavController,
    viewModel: NearbyLandmarkViewModel = hiltViewModel(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(snackbarHostState) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is NearbyLandmarkUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ),
    ) {
        // Location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
        val longitude = rememberSaveable {
            mutableStateOf(0.0)
        }

        val latitude = rememberSaveable {
            mutableStateOf(0.0)
        }

        val permissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            LaunchedEffect(Unit) {
                permissionState.launchMultiplePermissionRequest()
            }
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            longitude.value = location?.longitude ?: 0.0
            latitude.value = location?.latitude ?: 0.0
        }

        viewModel.onEvent(NearbyLandmarkEvent.LandedOnScreen(Geolocation(longitude = longitude.value, latitude = latitude.value)))
        val state by viewModel.state.collectAsState()
        val cached by viewModel.cached.collectAsState()

        Spacer(modifier = Modifier.size(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    viewModel.onEvent(NearbyLandmarkEvent.SaveFetchedLandmarksToDb)
                }
            ) {
                Text(text = stringResource(R.string.save_pois))
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            DropdownMenuFoursquareApi(
                onClick = {
                    viewModel.onEvent(NearbyLandmarkEvent.FetchNearbyLandmarks(it))
                }
            )
        }

        if (state.isLoading) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)

            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)

            ) {

                LazyColumn {
                    itemsIndexed(state.nearby_landmark_list) { index, item ->
                        Spacer(modifier = Modifier.size(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            ExpandingText(modifier = Modifier, text = item)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = {
                                navController.navigate(Screen.MapScreen.withStringArgs(item.substringAfter("lat:").substringBefore("\n"), item.substringAfter("lng:").substringBefore("\n"), item.substringBefore("address")))
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_zoom_in_24),
                                    contentDescription = "map_btn",
                                    tint = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(10.dp))
                        Divider(color = Color.Gray)
                    }
                }

            }
        }
    }
}