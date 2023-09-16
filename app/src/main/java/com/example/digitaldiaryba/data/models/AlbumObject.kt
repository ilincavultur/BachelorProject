package com.example.digitaldiaryba.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_object_table")
data class AlbumObject(
    @PrimaryKey(autoGenerate = true)
    var albumId: Int,
    var name: String,
    var description: String,
    var coverPhotoUri: String
)