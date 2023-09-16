package com.example.digitaldiaryba.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.digitaldiaryba.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaObjectDao {

    @Insert
    suspend fun insertMedia(media: MediaObject) : Long

    @Insert
    suspend fun insertBuildingInfo(buildingInfo: BuildingInfoObject)

    @Transaction
    suspend fun insertImageAndCorrespondingBuildingInfoState(media: MediaObject, buildingInfoObject: BuildingInfoObject) {
        val mediaId = insertMedia(media)
        buildingInfoObject.mediaId = mediaId.toInt()
        insertBuildingInfo(buildingInfoObject)
    }

    @Transaction
    suspend fun insertVideoAndCorrespondingBuildingInfoState(media: MediaObject, buildingInfoObject: BuildingInfoObject) {
        val mediaId = insertMedia(media)
        buildingInfoObject.mediaId = mediaId.toInt()
        insertBuildingInfo(buildingInfoObject)
    }

    @Transaction
    suspend fun insertMediaAndCorrespondingAudioObject(media: MediaObject) {
        insertMedia(media)
    }

    @Update
    suspend fun updateMedia(media: MediaObject)

    @Query("SELECT * FROM media_object_table")
    fun getAllMedias(): Flow<List<MediaObject>>

    @Query("SELECT * FROM media_object_table WHERE mediaId=:id")
    fun getMediaObject(id: Int): Flow<MediaObject>

    @Query("SELECT description FROM media_object_table WHERE mediaId=:id")
    fun getMediaDescription(id: Int): Flow<String>

    @Query("SELECT decodedUri FROM media_object_table WHERE mediaId=:id")
    fun getMediaDecodedUri(id: Int): Flow<String>

    @Query("select * from media_object_table where album_object_id=:albumId")
    fun getAllMediasFromAnAlbum(albumId: Int): Flow<List<MediaObject>>

    @Query("SELECT building_info_object_table.buildingInfoObjectName FROM building_info_object_table " +
            "INNER JOIN media_object_table ON media_object_table.mediaId = building_info_object_table.media_object_id " +
            "WHERE media_object_table.album_object_id = :albumId")
    fun getBuildingNamesForMedias(albumId: Int): Flow<List<String>>

    @Query("SELECT * FROM building_info_object_table " +
            "INNER JOIN media_object_table ON media_object_table.mediaId = building_info_object_table.media_object_id " +
            "WHERE media_object_table.album_object_id = :albumId")
    fun getBuildingInfosForMedias(albumId: Int): Flow<List<BuildingInfoObject>>

    @Query("SELECT * FROM media_object_table WHERE mediaId IN (:mediaObjectIds)")
    fun loadAllMediasByIds(mediaObjectIds: IntArray): LiveData<List<MediaObject>>

    @Insert
    suspend fun insertAllMedias(mediaobjects: MediaObject)

    @Delete
    suspend fun deleteMedia(mediaObject: MediaObject)
}