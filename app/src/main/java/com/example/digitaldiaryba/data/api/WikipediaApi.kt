package com.example.digitaldiaryba.data.api

import com.example.digitaldiaryba.data.models.responses.wikipedia.WikipediaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface WikipediaApi {
    @Headers(
        "Accept: application/json"
    )
    @GET("summary/{buildingName}")
    suspend fun getWikipediaDescription(@Path("buildingName") name: String) : Response<WikipediaResponse>
}
