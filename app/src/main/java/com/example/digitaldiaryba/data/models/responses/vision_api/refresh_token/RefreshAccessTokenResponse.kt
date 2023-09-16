package com.example.digitaldiaryba.data.models.responses.vision_api.refresh_token

data class RefreshAccessTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val scope: String,
    val token_type: String
)