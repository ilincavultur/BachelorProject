package com.example.digitaldiaryba.data.models.responses.foursquare_api

data class FoursquareApiResult(
    val categories: List<Category>,
    val chains: List<Chain>,
    val distance: Int,
    val fsq_id: String,
    val geocodes: Geocodes,
    val link: String,
    val location: Location,
    val name: String,
    val related_places: RelatedPlaces,
    val timezone: String
)