package com.example.digitaldiaryba.data.api

import com.example.digitaldiaryba.data.models.responses.vision_api.refresh_token.RefreshAccessTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoogleOAuthApiService {
    @POST("o/oauth2/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String
    ): Response<RefreshAccessTokenResponse>
}
