package com.example.digitaldiaryba.data.models.responses.vision_api.landmark_recognition

data class LandmarkAnnotation(
    val boundingPoly: BoundingPoly,
    val description: String,
    val locations: List<Location>,
    val mid: String,
    val score: Double
)