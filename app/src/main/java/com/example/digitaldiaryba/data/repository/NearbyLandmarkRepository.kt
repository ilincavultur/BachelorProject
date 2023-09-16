package com.example.digitaldiaryba.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.digitaldiaryba.data.database.MediaObjectDao
import com.example.digitaldiaryba.data.database.NearbyLandmarkDao
import com.example.digitaldiaryba.data.models.NearbyLandmarkObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

class NearbyLandmarkRepository @Inject constructor(
    private val nearbyLandmarkDao: NearbyLandmarkDao
) {
    suspend fun insertLandmarkList(landmarks: NearbyLandmarkObject) {
        nearbyLandmarkDao.insertLandmarkList(landmarks)
    }

    fun getLatestLandmarkList(type: String): Flow<NearbyLandmarkObject> {
        Log.d(TAG, "getLatestLandmarkList: i m retrieving cached values "+ type)
        return nearbyLandmarkDao.getLatestLandmarkList(type)
    }

}