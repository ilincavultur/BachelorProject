package com.example.digitaldiaryba.data.api

//import com.example.digitaldiaryba.data.models.responses.dbpedia.BuildingInfo
import com.example.digitaldiaryba.data.models.responses.dbpedia.DBpediaResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface DBpediaService {
    @Headers("Accept: application/sparql-results+json")
    @GET("sparql")
    suspend fun getBuildingInfo(@Query("query") queryString: String): Response<DBpediaResponse>
}