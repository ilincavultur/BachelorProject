package com.example.digitaldiaryba.data.database

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.digitaldiaryba.data.models.*
import com.example.digitaldiaryba.util.ListTypeConverter
import com.example.digitaldiaryba.util.BuildingInfoObjectConverter

@androidx.room.Database(entities = [AlbumObject::class, MediaObject::class, PresentationObject::class, BuildingInfoObject::class, NearbyLandmarkObject::class], version = 4)
@TypeConverters(ListTypeConverter::class, BuildingInfoObjectConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun albumObjectDao(): AlbumObjectDao
    abstract fun mediaObjectDao(): MediaObjectDao
    abstract fun presentationObjectDao(): PresentationObjectDao
    abstract fun buildingInfoDao(): BuildingInfoObjectDao
    abstract fun nearbyLandmarkDao(): NearbyLandmarkDao
}