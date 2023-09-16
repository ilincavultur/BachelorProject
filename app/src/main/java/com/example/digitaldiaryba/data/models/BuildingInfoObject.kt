package com.example.digitaldiaryba.data.models

import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.digitaldiaryba.ui.saved_presentations.PresentationListItem
import com.example.digitaldiaryba.ui.saved_presentations.video_playback.VideoItem
import com.example.digitaldiaryba.util.enums.EMediaType

@Entity(
    tableName = "building_info_object_table",
    foreignKeys = [
        ForeignKey(
            entity = MediaObject::class,
            parentColumns = ["mediaId"],
            childColumns = ["media_object_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BuildingInfoObject(
    @PrimaryKey(autoGenerate = true)
    val buildingInfoId: Int?,
    @ColumnInfo(name = "media_object_id", index = true)
    var mediaId: Int,
    var architect: String,
    var country: String,
    var location: String,
    var description: String,
    var year: String,
    var buildingInfoObjectName: String,
    var filename: String,
    var fileUri: String,
    var longitude: String,
    var latitude: String,
    var confidenceScore: String,
    var mediaType: EMediaType
) {
    fun toPresentationListItem() : PresentationListItem {
        Log.d(TAG, "toPresentationListItem: name " + buildingInfoObjectName)
        return PresentationListItem(
            title = if (buildingInfoObjectName.isNotEmpty()) {buildingInfoObjectName} else {filename} ,
            architect = architect,
            location = location,
            year = year,
            imgUri = fileUri,
            type = mediaType
        )
    }

    fun toVideoItem() : VideoItem? {
        Log.d(TAG, "toPresentationListItem: name " + buildingInfoObjectName)
        if (this.mediaType == EMediaType.VIDEO) {
            return VideoItem(
                id = 0,
                mediaUrl = fileUri,
                thumbnail = fileUri
            )
        }
        return null
    }
}
