package com.example.digitaldiaryba.ui.nearby_landmark

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.data.api.FoursquareApi
import com.example.digitaldiaryba.data.models.NearbyLandmarkObject
import com.example.digitaldiaryba.data.models.responses.foursquare_api.FoursquareApiResult
import com.example.digitaldiaryba.data.repository.NearbyLandmarkRepository
import com.example.digitaldiaryba.ui.map.MapUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.toImmutableList
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NearbyLandmarkViewModel @Inject constructor(
    private val nearbyLandmarkRepository: NearbyLandmarkRepository,
    private val foursquareApi: FoursquareApi,
) : ViewModel() {

    private val nearbylandmarkslist = MutableStateFlow(NearbyLandmarkListState())
    val nearbylandmarkslisttype = MutableStateFlow(NearbyLandmarkListTypeState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cached = nearbylandmarkslisttype.flatMapLatest {
        if (it.nearbyLandmarkListType.isEmpty()) {
            flow {  }
        } else {
            nearbyLandmarkRepository.getLatestLandmarkList(it.nearbyLandmarkListType).mapLatest { obj ->
                if (obj != null) {
                    obj.landmark_list
                } else {
                    emptyList()
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList<String>())

    private val _state = MutableStateFlow(NearbyLandmarkState())
    val state = combine(_state, nearbylandmarkslisttype, cached, nearbylandmarkslist ) { state, type, cached, nearbyLandmarksList ->
        state.copy(
            nearby_landmark_list_type = type.nearbyLandmarkListType,
            nearby_landmark_list = nearbyLandmarksList.nearbyLandmarkList.ifEmpty { cached },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NearbyLandmarkState())

    private val _eventFlow = MutableSharedFlow<NearbyLandmarkUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private fun mapFoursquareApiResponse(results: List<FoursquareApiResult>) : List<String> {
        val foursquareMap = mutableListOf<String>()
        results.forEach {
            if(it.name.isNotEmpty()) {
                Log.d(TAG, "mapFoursquareApiResponse: name " + it.name)
                foursquareMap.add(it.name + "\n" + "address: " + if (!it.location.address.isNullOrEmpty()) {it.location.address + "\n"} else {
                    "unknown\n"
                } + "distance: " + it.distance + "m\n" +
                        "lat:" + it.geocodes.main.latitude + "\nlng:" +  it.geocodes.main.longitude
                )
            }
        }
        return foursquareMap
    }

    fun onEvent(event: NearbyLandmarkEvent) {
        when (event) {
            is NearbyLandmarkEvent.FetchNearbyLandmarks -> {
                _state.update { it.copy(
                    isLoading = true,
                    nearby_landmark_list_type = event.categories
                ) }

                nearbylandmarkslisttype.update {
                    it.copy(
                        nearbyLandmarkListType = event.categories
                    )
                }

                Log.d(TAG, "onEvent: selected category " + nearbylandmarkslisttype.value)

                nearbylandmarkslist.update {
                    it.copy(
                        nearbyLandmarkList = emptyList()
                    )
                }

                viewModelScope.launch (Dispatchers.Default) {
                    val response = try {
                        foursquareApi.getNearestLandmarks(sort = "DISTANCE", categories = event.categories)
                    } catch (e: IOException) {
                        Log.d(TAG, "IOException: " + e.toString())
                        _eventFlow.emit(
                            NearbyLandmarkUiEvent.ShowSnackbar(
                                "Check your internet connection!"
                            )
                        )
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(
                                isLoading = false
                            ) }
                        }
                        return@launch
                    } catch (e: HttpException) {
                        Log.d(TAG, "HttpException: " + e.toString())
                        _eventFlow.emit(
                            NearbyLandmarkUiEvent.ShowSnackbar(
                                "An error has occurred"
                            )
                        )
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(
                                isLoading = false
                            ) }
                        }
                        return@launch
                    }

                    if (response.isSuccessful && response.body() != null) {
                        Log.d(TAG, "response successful " + response.body())
                        if (response.body()!!.results.isNotEmpty()) {
                            val foursquareApiMap = mapFoursquareApiResponse(response.body()!!.results)

                            nearbylandmarkslist.update {
                                it.copy(
                                    nearbyLandmarkList = foursquareApiMap
                                )
                            }

                        } else {
                            _eventFlow.emit(
                                NearbyLandmarkUiEvent.ShowSnackbar(
                                    "Results not found"
                                )
                            )
                        }
                    }

                    withContext(Dispatchers.Main) {
                        _state.update { it.copy(
                            isLoading = false
                        ) }
                    }
                }
            }
            is NearbyLandmarkEvent.LandedOnScreen -> {
                _state.update {
                    it.copy(
                        userCurLocation = event.geolocation
                    )
                }
                Log.d(TAG, "onEvent: user cur location longitude" + event.geolocation.longitude + " latitude" + event.geolocation.latitude)
            }
            NearbyLandmarkEvent.SaveFetchedLandmarksToDb -> {
                if (state.value.nearby_landmark_list.isNotEmpty()) {
                    Log.d(TAG, "onEvent: i am saving nearby landmark list!")
                    val newObject = NearbyLandmarkObject(
                        landmark_list_id = null,
                        landmark_list = state.value.nearby_landmark_list,
                        category = state.value.nearby_landmark_list_type
                    )

                    viewModelScope.launch {
                        nearbyLandmarkRepository.insertLandmarkList(newObject)
                    }
                }
            }
        }
    }
}