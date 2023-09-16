package com.example.digitaldiaryba.data.database

import androidx.room.*
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingInfoObjectDao {
    @Insert
    suspend fun insertBuildingInfo(buildingInfo: BuildingInfoObject)

    @Update
    suspend fun updateBuildingInfo(buildingInfo: BuildingInfoObject)

    @Query("SELECT * FROM building_info_object_table WHERE media_object_id=:id")
    fun getBuildingInfo(id: Int): Flow<BuildingInfoObject>

    @Query("SELECT buildingInfoId FROM building_info_object_table WHERE media_object_id=:id")
    fun getBuildingInfoId(id: Int): Flow<Int>

    @Delete
    suspend fun deleteBuildingInfoObject(buildingInfo: BuildingInfoObject)

}