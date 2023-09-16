package com.example.digitaldiaryba.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.digitaldiaryba.util.ListTypeConverter

@Entity(tableName = "nearby_landmark_object")
data class NearbyLandmarkObject(
    @PrimaryKey (autoGenerate = true)
    val landmark_list_id: Int?,
    @TypeConverters(ListTypeConverter::class)
    var landmark_list: List<String>,
    var category: String
)