package com.example.digitaldiaryba.ui.file_detail

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.BuildConfig
import com.example.digitaldiaryba.data.api.DBpediaService
import com.example.digitaldiaryba.data.api.GoogleOAuthApiService
import com.example.digitaldiaryba.data.api.VisionApi
import com.example.digitaldiaryba.data.api.WikipediaApi
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.data.models.responses.dbpedia.Binding
import com.example.digitaldiaryba.data.repository.BuildingInfoRepository
import com.example.digitaldiaryba.data.repository.MediaRepository

import com.example.digitaldiaryba.util.enums.EMediaType
import com.example.digitaldiaryba.util.chooseQueryString
import com.example.digitaldiaryba.util.chooseWikiName
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.log


@ExperimentalCoroutinesApi
@HiltViewModel
class FileDetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val buildingInfoRepository: BuildingInfoRepository,
    private val dbpediaService: DBpediaService,
    private val googleOAuthService: GoogleOAuthApiService,
    private val googleVisionApiService: VisionApi,
    private val wikipediaService: WikipediaApi
) : ViewModel() {
    private val _albumId = MutableStateFlow(0)
    private val _mediaId = MutableStateFlow(0)

    private val landmarkInfoMap = MutableStateFlow(mapOf<String, String>())
    private val dbpediaInfoMap = MutableStateFlow(mapOf<String, String>())

    private val _buildingInfoState = MutableStateFlow(BuildingInfoUiState())
    private val buildingInfoState = _buildingInfoState

    val _buildingInfoId = _mediaId.flatMapLatest {
        if (it == 0) {
            emptyFlow()
        } else {
            buildingInfoRepository.getBuildingInfoId(it)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val _cached = _mediaId.flatMapLatest {
        if (it == 0) {
            emptyFlow()
        } else {
            buildingInfoRepository.getBuildingInfo(it)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), BuildingInfoObject(0, 0, "", "", "", "", "", "", "", "", "", "", "", mediaType = EMediaType.IMAGE))

    val _mediaObject = _mediaId.flatMapLatest {
        if (it == 0) {
            emptyFlow()
        } else {
            mediaRepository.getMediaObjectById(it)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MediaObject(null, 0, EMediaType.IMAGE, "", "", "", "", "", "", "", gsUri = "", filepath = ""))

    private var _mediaDecodedUri = _mediaId.flatMapLatest {
        if (it == 0) {
            emptyFlow()
        } else {
            mediaRepository.getMediaDecodedUri(it)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private val _infoList = MutableLiveData(InfoListState())
    val infoList = _infoList

    private val _eventFlow = MutableSharedFlow<FileDetailsUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = MutableStateFlow(FileDetailsState())
    val state = combine(_state, _albumId, _mediaId, _mediaDecodedUri) { fileDetailsState, albumId, mediaId, mediaDecodedUri->
        fileDetailsState.copy(
            mediaId = mediaId,
            albumId = albumId,
            uri = mediaDecodedUri,
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), FileDetailsState())

    private suspend fun fetchRefreshAccessToken() = withContext(Dispatchers.Default) {
        val response = try {
            googleOAuthService.refreshToken(
                grantType = "refresh_token",
                clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID,
                clientSecret = BuildConfig.GOOGLE_OAUTH_CLIENT_SECRET,
                refreshToken = BuildConfig.GOOGLE_OAUTH_CLIENT_REFRESH_TOKEN
            )
        } catch (e: IOException) {
            Log.d(TAG, "IOException: " + e.toString())
            _eventFlow.emit(
                FileDetailsUiEvent.ShowSnackbar(
                    "Check internet connection"
                )
            )
            withContext(Dispatchers.Main) {
                _state.update { it.copy(
                    onError = true,
                    resultsNotFound = false,
                    dbpediaButtonIsEnabled = false
                ) }
            }

            return@withContext
        } catch (e: HttpException) {
            Log.d(TAG, "HttpException: " + e.toString())
            _eventFlow.emit(
                FileDetailsUiEvent.ShowSnackbar(
                    "An error has occured"
                )
            )

            withContext(Dispatchers.Main) {
                _state.update { it.copy(
                    onError = true,
                    resultsNotFound = false,
                    dbpediaButtonIsEnabled = false
                ) }
            }

            return@withContext
        }
        if (response.isSuccessful && response.body() != null) {
            if (response.body()!!.access_token.isNotEmpty()) {
            Log.d("Cloud Vision API success json parsed", response.body()!!.access_token)
                _state.update { it.copy(
                    refreshAccessToken = response.body()!!.access_token,
                ) }
            } else {
                _eventFlow.emit(
                    FileDetailsUiEvent.ShowSnackbar(
                        "Access token couldn't be fetched"
                    )
                )
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(
                        onError = true,
                        resultsNotFound = false,
                        dbpediaButtonIsEnabled = false
                    ) }
                }

            }
        }
    }

    private suspend fun fetchLandmarkInfo() = withContext(Dispatchers.Default) {
        Log.d(TAG, "onEvent:task.isSuccessful " + state.value.gsUri)
            val json = "{\n" +
                    "  \"requests\": [\n" +
                    "    {\n" +
                    "      \"image\": {\n" +
                    "        \"source\": {\n" +
                    "          \"gcsImageUri\": \"${state.value.gsUri}\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"features\": [\n" +
                    "        {\n" +
                    "          \"maxResults\": 1,\n" +
                    "          \"type\": \"LANDMARK_DETECTION\"\n" +
                    "        },\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}"
            val requestBody = RequestBody.create("application/json".toMediaType(), json)

            val response = try {
                googleVisionApiService.annotateImage(
                    "Bearer ${state.value.refreshAccessToken}",
                    requestBody
                )
            } catch (e: IOException) {
                Log.d(TAG, "IOException: " + e.toString())
                _eventFlow.emit(
                    FileDetailsUiEvent.ShowSnackbar(
                        "Check internet connection"
                    )
                )
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(
                        onError = true,
                        resultsNotFound = false,
                        dbpediaButtonIsEnabled = false
                    ) }
                }
                return@withContext
            } catch (e: HttpException) {
                Log.d(TAG, "HttpException: " + e.toString())
                _eventFlow.emit(
                    FileDetailsUiEvent.ShowSnackbar(
                        "An error has occured"
                    )
                )
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(
                        onError = true,
                        resultsNotFound = false,
                        dbpediaButtonIsEnabled = false
                    ) }
                }

                return@withContext
            }

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "fetchLandmarkInfo: response.isSuccessful")
                if (!response.body()!!.responses.isNullOrEmpty() && !response.body()!!.responses[0].landmarkAnnotations.isNullOrEmpty() ) {
                    Log.d(TAG, "fetchLandmarkInfo: response " + response.body().toString())
                    landmarkInfoMap.value = mapOf(
                        Pair("Building Name:", response.body()!!.responses[0].landmarkAnnotations[0].description.toString()),
                        Pair("Confidence Score:", response.body()!!.responses[0].landmarkAnnotations[0].score.toString()),
                        Pair("Latitude:", response.body()!!.responses[0].landmarkAnnotations[0].locations[0].latLng.latitude.toString()),
                        Pair("Longitude:", response.body()!!.responses[0].landmarkAnnotations[0].locations[0].latLng.longitude.toString()),
                    )

                    _infoList.postValue(InfoListState(landmarkInfoMap.value))
                    _buildingInfoState.update {
                        it.copy(
                            name = response.body()!!.responses[0].landmarkAnnotations[0].description,
                            confidenceScore = response.body()!!.responses[0].landmarkAnnotations[0].score.toString(),
                            latitude = response.body()!!.responses[0].landmarkAnnotations[0].locations[0].latLng.latitude.toString(),
                            longitude = response.body()!!.responses[0].landmarkAnnotations[0].locations[0].latLng.longitude.toString(),
                        )
                    }
                } else {
                    _eventFlow.emit(
                        FileDetailsUiEvent.ShowSnackbar(
                            "No results were found"
                        )
                    )
                    withContext(Dispatchers.Main) {
                        _state.update {
                            it.copy(
                                resultsNotFound = true,
                                onError = false,
                                dbpediaButtonIsEnabled = false
                            )
                        }
                    }
                }

            }
    }

    private fun mapDBpediaResponse(bindings: List<Binding>) : Map<String, String> {
        val dbpediamap = mutableMapOf<String, String>()
        bindings.forEach {
            if(it.name != null) {
                dbpediamap["name"] = processUiText(it.name.value)
            }
            if(it.architect != null) {
                dbpediamap["architect"] = processUiText(it.architect.value)
            }
            if(it.location != null) {
                dbpediamap["location"] = processUiText(it.location.value)
            }
            if(it.year != null) {
                dbpediamap["year"] = processUiText(it.year.value)
            }
            if(it.description != null) {
                dbpediamap["description"] = it.description.value
            }
        }
        return dbpediamap
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun onEvent(event: FileDetailsEvent) {
        when(event) {
            FileDetailsEvent.UpdateMedia -> {

                val newObject = MediaObject(
                    _mediaObject.value.mediaId,
                    _mediaObject.value.albumId,
                    _mediaObject.value.type,
                    _mediaObject.value.name,
                    "",
                    _mediaObject.value.date,
                    _mediaObject.value.time,
                    _mediaObject.value.locationString,
                    _mediaObject.value.decodedUri,
                    _mediaObject.value.uri,
                    _mediaObject.value.gsUri,
                    _mediaObject.value.filepath
                )
                viewModelScope.launch {
                    mediaRepository.updateMedia(newObject)
                }

            }
            is FileDetailsEvent.LandedOnScreen -> {

                _mediaId.value = event.mediaId

                if (_mediaObject.value.type == EMediaType.IMAGE) {
                    // "Download" file from storage
                    val storageReference = Firebase.storage.reference;
                    val file = Uri.fromFile(File(_mediaObject.value.filepath))
                    val photoReference = storageReference.child("${file.lastPathSegment}")

                    if (_cached.value.buildingInfoObjectName.isNotEmpty()) {
                        _state.update { it.copy(
                            dbpediaButtonIsEnabled = true
                        ) }
                    }

                    _buildingInfoState.update {
                        it.copy(
                            mediaId = event.mediaId,
                            buildingInfoId = _buildingInfoId.value
                        )
                    }

                    _state.update { it.copy(
                        gsUri = Firebase.storage.getReferenceFromUrl(photoReference.toString()).toString(),
                        landmarkinfoButtonIsEnabled = true
                    ) }
                    Log.d(TAG, "onEvent: LandedOnScreen ! " + _state.value.gsUri)

                }
            }
            is FileDetailsEvent.FetchDBpediaInfo -> {
                if (_cached.value.description.isNotEmpty()) {
                    _state.update { it.copy(
                        dbpediaButtonIsEnabled = true,
                        isLoading = false,
                        onError = false,
                        resultsNotFound = false
                    ) }

                    dbpediaInfoMap.value = mapOf(
                        Pair("Architect:", _cached.value.architect),
                        Pair("Location:", _cached.value.location),
                        Pair("Year Built:", _cached.value.year),
                        Pair("About:", _cached.value.description)
                    )

                    _buildingInfoState.update { it.copy(
                        architect = _cached.value.architect,
                        location = _cached.value.location,
                        year = _cached.value.year,
                        description = _cached.value.description,
                    ) }

                    _infoList.postValue(InfoListState(dbpediaInfoMap.value))
                } else {
                    _state.update { it.copy(
                        isLoading = true,
                        onError = false,
                        resultsNotFound = false
                    ) }
                    viewModelScope.launch (Dispatchers.Default) {
                        val response = try {
                            dbpediaService.getBuildingInfo(chooseQueryString(_buildingInfoState.value.name!!))
                        } catch (e: IOException) {
                            Log.d(TAG, "IOException: " + e.toString())
                            _eventFlow.emit(
                                FileDetailsUiEvent.ShowSnackbar(
                                    "Check internet connection"
                                )
                            )
                            withContext(Dispatchers.Main) {
                                _state.update { it.copy(
                                    onError = true,
                                    resultsNotFound = false
                                ) }
                            }
                            return@launch
                        } catch (e: HttpException) {
                            Log.d(TAG, "HttpException: " + e.toString())
                            _eventFlow.emit(
                                FileDetailsUiEvent.ShowSnackbar(
                                    "An error has occured"
                                )
                            )
                            withContext(Dispatchers.Main) {
                                _state.update { it.copy(
                                    onError = true,
                                    resultsNotFound = false
                                ) }
                            }
                            return@launch
                        }
                        if (response.isSuccessful && response.body() != null) {
                            Log.d(TAG, "response successful " + response.body())
                            if (response.body()!!.results.bindings.isNotEmpty()) {
                                val dbpediamap = mapDBpediaResponse(response.body()!!.results.bindings)
                                _buildingInfoState.update { it.copy(
                                    architect = dbpediamap["architect"].toString(),
                                    location = dbpediamap["location"].toString(),
                                    year = dbpediamap["year"].toString(),
                                    description = dbpediamap["description"].toString(),
                                ) }

                                dbpediaInfoMap.value = mapOf(
                                    Pair("Building Name:", buildingInfoState.value.name!!),
                                    Pair("Architect:", buildingInfoState.value.architect!!),
                                    Pair("Location:", buildingInfoState.value.location!!),
                                    Pair("Year Built:", buildingInfoState.value.year!!),
                                    Pair("About:", buildingInfoState.value.description!!),
                                )

                                _infoList.postValue(InfoListState(dbpediaInfoMap.value))

                                val newObject = BuildingInfoObject(
                                    buildingInfoId = _buildingInfoState.value.buildingInfoId,
                                    mediaId = _buildingInfoState.value.mediaId,
                                    architect = _buildingInfoState.value.architect,
                                    country = "",
                                    location = _buildingInfoState.value.location,
                                    description = _buildingInfoState.value.description,
                                    year = _buildingInfoState.value.year,
                                    buildingInfoObjectName = _buildingInfoState.value.name,
                                    longitude = _buildingInfoState.value.longitude,
                                    latitude = _buildingInfoState.value.latitude,
                                    confidenceScore = _buildingInfoState.value.confidenceScore,
                                    filename = _buildingInfoState.value.filename,
                                    fileUri = _mediaObject.value.decodedUri,
                                    mediaType = EMediaType.IMAGE
                                )
                                buildingInfoRepository.updateBuildingInfo(newObject)
                            } else {
                                withContext(Dispatchers.Main) {
                                    _state.update { it.copy(
                                        resultsNotFound = true,
                                        onError = false
                                    ) }
                                }
                            }
                        } else {
                            return@launch
                        }
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(
                                isLoading = false,
                                onError = false,
                                resultsNotFound = false
                            ) }
                        }
                    }

                    _state.update { it.copy(
                        isLoading = true,
                        onError = false,
                        resultsNotFound = false
                    ) }
                    viewModelScope.launch (Dispatchers.Default) {
                        val response = try {
                            wikipediaService.getWikipediaDescription(chooseWikiName(_buildingInfoState.value.name.toString()))
                        } catch (e: IOException) {
                            Log.d(TAG, "IOException: " + e.toString())
                            _eventFlow.emit(
                                FileDetailsUiEvent.ShowSnackbar(
                                    "Check internet connection"
                                )
                            )
                            withContext(Dispatchers.Main) {
                                _state.update { it.copy(
                                    onError = true,
                                    resultsNotFound = false
                                ) }
                            }

                            return@launch
                        } catch (e: HttpException) {
                            Log.d(TAG, "HttpException: " + e.toString())
                            _eventFlow.emit(
                                FileDetailsUiEvent.ShowSnackbar(
                                    "An error has occured"
                                )
                            )
                            withContext(Dispatchers.Main) {
                                _state.update { it.copy(
                                    onError = true,
                                    resultsNotFound = false
                                ) }
                            }

                            return@launch
                        }
                        if (response.isSuccessful && response.body() != null) {
                            Log.d(TAG, "response successful " + response.body())
                            if (response.body()!!.extract.isNotEmpty()) {
                                Log.d(TAG, "onEvent: success extract wikipedia " + response.body()!!.extract)
                                //val dbpediamap = mapDBpediaResponse(response.body()!!.results.bindings)
                                _buildingInfoState.update { it.copy(
                                    description = response.body()!!.extract.toString(),
                                ) }

                                dbpediaInfoMap.value = mapOf(
                                    Pair("Building Name:", buildingInfoState.value.name!!),
                                    Pair("Architect:", buildingInfoState.value.architect!!),
                                    Pair("Location:", buildingInfoState.value.location!!),
                                    Pair("Year Built:", buildingInfoState.value.year!!),
                                    Pair("About:", buildingInfoState.value.description!!),
                                )

                                _infoList.postValue(InfoListState(dbpediaInfoMap.value))

                                val newObject = BuildingInfoObject(
                                    buildingInfoId = _buildingInfoState.value.buildingInfoId,
                                    mediaId = _buildingInfoState.value.mediaId,
                                    architect = _buildingInfoState.value.architect,
                                    country = "",
                                    location = _buildingInfoState.value.location,
                                    description = _buildingInfoState.value.description,
                                    year = _buildingInfoState.value.year,
                                    buildingInfoObjectName = _buildingInfoState.value.name,
                                    longitude = _buildingInfoState.value.longitude,
                                    latitude = _buildingInfoState.value.latitude,
                                    confidenceScore = _buildingInfoState.value.confidenceScore,
                                    filename = _buildingInfoState.value.filename,
                                    fileUri = _mediaObject.value.decodedUri,
                                    mediaType = EMediaType.IMAGE
                                )
                                buildingInfoRepository.updateBuildingInfo(newObject)
                            } else {
                                _eventFlow.emit(
                                    FileDetailsUiEvent.ShowSnackbar(
                                        "No results were found"
                                    )
                                )
                                withContext(Dispatchers.Main) {
                                    _state.update { it.copy(
                                        resultsNotFound = true,
                                        onError = false
                                    ) }
                                }

                            }
                        } else {
                            _eventFlow.emit(
                                FileDetailsUiEvent.ShowSnackbar(
                                    "No results were found"
                                )
                            )
                            //return@launch
                        }
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(
                                isLoading = false,
                                onError = false,
                                resultsNotFound = false
                            ) }
                        }
                    }
                }

            }
            is FileDetailsEvent.FetchLandmarkInfo -> {

                    if (_cached.value.buildingInfoObjectName.isNotEmpty() && !event.refresh) {

                        _state.update { it.copy(
                            dbpediaButtonIsEnabled = true,
                            isLoading = false,
                            onError = false,
                            resultsNotFound = false
                        ) }

                        landmarkInfoMap.value = mapOf(
                            Pair("Building Name:", _cached.value.buildingInfoObjectName),
                            Pair("Confidence Score:", _cached.value.confidenceScore),
                            Pair("Latitude:", _cached.value.latitude),
                            Pair("Longitude:", _cached.value.longitude),
                        )

                        _buildingInfoState.update {
                            it.copy(
                                name = _cached.value.buildingInfoObjectName,
                                confidenceScore = _cached.value.confidenceScore,
                                latitude = _cached.value.latitude,
                                longitude = _cached.value.longitude,
                            )
                        }

                        _infoList.postValue(InfoListState(landmarkInfoMap.value))
                        Log.d(TAG, "fetchLandmarkInfo: cached value: " + _cached.value.buildingInfoObjectName)
                    } else {
                        _state.update { it.copy(
                            isLoading = true,
                            onError = false,
                            resultsNotFound = false
                        ) }
                        viewModelScope.launch (Dispatchers.Default) {
                            fetchRefreshAccessToken()
                            fetchLandmarkInfo()

                            val newObject = BuildingInfoObject(
                                buildingInfoId = _buildingInfoState.value.buildingInfoId,
                                mediaId = _buildingInfoState.value.mediaId,
                                architect = _buildingInfoState.value.architect,
                                country = "",
                                location = _buildingInfoState.value.location,
                                description = _buildingInfoState.value.description,
                                year = _buildingInfoState.value.year,
                                buildingInfoObjectName = _buildingInfoState.value.name,
                                longitude = _buildingInfoState.value.longitude,
                                latitude = _buildingInfoState.value.latitude,
                                confidenceScore = _buildingInfoState.value.confidenceScore,
                                filename = _mediaObject.value.name.toString(),
                                fileUri = _mediaObject.value.decodedUri,
                                mediaType = EMediaType.IMAGE
                            )
                            buildingInfoRepository.updateBuildingInfo(newObject)

                            withContext(Dispatchers.Main) {
                                _state.update { it.copy(
                                    dbpediaButtonIsEnabled = !state.value.onError && !state.value.resultsNotFound,
                                    isLoading = false,
                                    onError = false,
                                    resultsNotFound = false
                                ) }
                            }
                        }
                    }
            }
        }
    }

    private fun processUiText(text: String) : String {
        var newText = text
        if (newText.contains("http")) {
            newText = newText.substringAfterLast("/")
        }
        if (newText.contains("_")) {
            newText = newText.replace("_", " ")
        }
        return newText
    }

}