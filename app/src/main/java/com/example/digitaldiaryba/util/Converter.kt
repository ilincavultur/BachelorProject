package com.example.digitaldiaryba.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BuildingInfoObjectConverter{
    @TypeConverter
    fun fromBuildingInfoObjectsJson(json: String): List<BuildingInfoObject> {
        return Gson().fromJson<ArrayList<BuildingInfoObject>>(
            json,
            object : TypeToken<ArrayList<BuildingInfoObject>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toBuildingInfoObjectJson(meanings: List<BuildingInfoObject>): String {
        return Gson().toJson(
            meanings,
            object : TypeToken<ArrayList<BuildingInfoObject>>(){}.type
        ) ?: "[]"
    }
}