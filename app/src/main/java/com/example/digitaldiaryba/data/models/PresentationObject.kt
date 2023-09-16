package com.example.digitaldiaryba.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.digitaldiaryba.util.BuildingInfoObjectConverter


@Entity(tableName = "presentation_object_table")
data class PresentationObject(
    @PrimaryKey(autoGenerate = true)
    var presentationId: Int,
    var name: String,
    var presentationUri: String,
    var coverPhotoUri: String,
    var subtitlePath: String,
    @TypeConverters(BuildingInfoObjectConverter::class)
    var buildingInfoList: List<BuildingInfoObject>
)