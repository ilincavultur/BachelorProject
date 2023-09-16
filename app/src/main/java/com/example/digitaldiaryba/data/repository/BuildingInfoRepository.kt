package com.example.digitaldiaryba.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.digitaldiaryba.data.database.BuildingInfoObjectDao
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuildingInfoRepository @Inject constructor(
    private val buildingInfoObjectDao: BuildingInfoObjectDao
) {

    suspend fun updateBuildingInfo(buildingInfo: BuildingInfoObject) {
        Log.d(TAG, "updateBuildingInfo: buildinginfoid" + buildingInfo.buildingInfoId)
        Log.d(TAG, "updateBuildingInfo: mediaId" + buildingInfo.mediaId)
        Log.d(TAG, "updateBuildingInfo: name" + buildingInfo.buildingInfoObjectName)
        buildingInfoObjectDao.updateBuildingInfo(buildingInfo)
    }

    fun getBuildingInfo(id: Int): Flow<BuildingInfoObject> {
        return buildingInfoObjectDao.getBuildingInfo(id)
    }

    fun getBuildingInfoId(id: Int): Flow<Int> {
        return buildingInfoObjectDao.getBuildingInfoId(id)
    }

}