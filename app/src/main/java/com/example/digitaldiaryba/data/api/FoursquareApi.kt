package com.example.digitaldiaryba.data.api

import com.example.digitaldiaryba.BuildConfig
import com.example.digitaldiaryba.data.models.responses.dbpedia.DBpediaResponse
import com.example.digitaldiaryba.data.models.responses.foursquare_api.FoursquareApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface FoursquareApi {

    @Headers("Accept: application/json", "Authorization: ${BuildConfig.FOURSQUARE_AUTH_TOKEN}")
    @GET("search")
    suspend fun getNearestLandmarks(@Query("sort") sort: String, @Query("categories") categories: String): Response<FoursquareApiResponse>

}