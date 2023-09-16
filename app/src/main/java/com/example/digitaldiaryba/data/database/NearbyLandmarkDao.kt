package com.example.digitaldiaryba.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.digitaldiaryba.data.models.AlbumObject
import com.example.digitaldiaryba.data.models.NearbyLandmarkObject
import kotlinx.coroutines.flow.Flow

@Dao
interface NearbyLandmarkDao {

    @Insert
    suspend fun insertLandmarkList(landmarks: NearbyLandmarkObject)

    @Update
    suspend fun updateLandmarkList(landmarks: NearbyLandmarkObject)

    @Query("SELECT * FROM nearby_landmark_object WHERE category=:type ORDER BY landmark_list_id DESC LIMIT 1")
    fun getLatestLandmarkList(type: String): Flow<NearbyLandmarkObject>

    @Delete
    suspend fun deleteLandmarkList(landmarks: NearbyLandmarkObject)
}