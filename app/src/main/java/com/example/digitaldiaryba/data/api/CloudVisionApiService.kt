package com.example.digitaldiaryba.data.api

import com.example.digitaldiaryba.data.models.responses.vision_api.landmark_recognition.VisionApiResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface VisionApi {
    @Headers(
        "x-goog-user-project: digital-diary-ba-170500"
    )
    @POST("v1/images:annotate")
    suspend fun annotateImage(
        @Header("Authorization") token: String,
        @Body body: RequestBody
    ): Response<VisionApiResponse>
}
