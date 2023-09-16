package com.example.digitaldiaryba.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.digitaldiaryba.data.models.AlbumObject
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.data.models.PresentationObject
import kotlinx.coroutines.flow.Flow

@Dao
interface PresentationObjectDao {
    @Insert
    suspend fun insertPresentation(presentation: PresentationObject)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM presentation_object_table WHERE presentationId=:id")
    fun getPresentationBuildingInfoList(id: Int): Flow<PresentationObject>

    @Update
    suspend fun updatePresentation(presentation: PresentationObject)

    @Query("SELECT * FROM presentation_object_table")
    fun getAllPresentations(): Flow<List<PresentationObject>>

    @Query("SELECT * FROM presentation_object_table WHERE name LIKE '%' || :search || '%'")
    fun searchPresentations(search: String?): Flow<List<PresentationObject>>

    @Query("SELECT * FROM presentation_object_table WHERE presentationId IN (:presentationObjectIds)")
    fun loadAllPresentationsByIds(presentationObjectIds: IntArray): LiveData<List<PresentationObject>>

    @Query("select * from media_object_table where album_object_id=:albumId")
    fun getAllMediasFromAnAlbum(albumId: Int): LiveData<List<MediaObject>>

    @Insert
    suspend fun insertAllPresentations(presentationObject: PresentationObject)

    @Delete
    suspend fun deletePresentation(presentationObject: PresentationObject)
}