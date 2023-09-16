package com.example.digitaldiaryba.ui.map

sealed interface MapEvent {
    data class ComputeRoute(val source: String, val destination: String) : MapEvent
}