package com.example.digitaldiaryba.ui.map

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.BuildConfig
import com.example.digitaldiaryba.ui.file_detail.FileDetailsUiEvent
import com.example.digitaldiaryba.ui.nearby_landmark.NearbyLandmarkState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(

) : ViewModel() {

    val path: MutableLiveData<MutableList<List<LatLng>>> = MutableLiveData(ArrayList())

    private val _state = MutableStateFlow(MapState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MapState())

    private val _eventFlow = MutableSharedFlow<MapUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.ComputeRoute -> {
                _state.update {
                    it.copy(
                        isLoading = true
                    )
                }
                // START https://developers.google.com/maps/documentation/directions/get-directions#TravelModes
                val client = OkHttpClient().newBuilder()
                    .build()

                val request: Request = Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/directions/json?origin=${event.source}&destination=${event.destination}&key=${BuildConfig.GOOGLE_MAPS_API_KEY}")
                    .get()
                    .build()
                viewModelScope.launch (Dispatchers.Default) {
                    try {
                        val response = client.newCall(request).execute()

                        // END https://developers.google.com/maps/documentation/directions/get-directions#TravelModes
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            // START https://lwgmnz.me/google-maps-and-directions-api-using-kotlin/
                            val jsonResponse = JSONObject(responseBody)
                            val routes = jsonResponse.getJSONArray("routes")
                            val legs = routes.getJSONObject(0).getJSONArray("legs")
                            val steps = legs.getJSONObject(0).getJSONArray("steps")
                            val newPath: MutableList<List<LatLng>> = ArrayList()
                            for (i in 0 until steps.length()) {
                                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                                newPath.add(PolyUtil.decode(points))
                            }
                            // END https://lwgmnz.me/google-maps-and-directions-api-using-kotlin/
                            path.postValue(newPath)

                            Log.d(TAG, "onEvent: responseBody " + responseBody.toString())
                            withContext(Dispatchers.Main) {
                                _state.update {
                                    it.copy(
                                        isLoading = false
                                    )
                                }
                            }
                        } else {
                            _eventFlow.emit(
                                MapUiEvent.ShowSnackbar(
                                    "An error has occurred"
                                )
                            )
                            withContext(Dispatchers.Main) {
                                _state.update {
                                    it.copy(
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    } catch (e: HttpException) {
                        _eventFlow.emit(
                            MapUiEvent.ShowSnackbar(
                                "An error has occurred"
                            )
                        )
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    } catch (e: IOException) {
                        _eventFlow.emit(
                            MapUiEvent.ShowSnackbar(
                                "Check internet connection"
                            )
                        )
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    }
                }

            }
        }
    }

}