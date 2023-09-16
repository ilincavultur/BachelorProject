package com.example.digitaldiaryba.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.digitaldiaryba.data.models.AlbumObject
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumObjectDao {
    @Insert
    suspend fun insertAlbum(album: AlbumObject)

    @Update
    suspend fun updateAlbum(album: AlbumObject)

    @Query("SELECT * FROM album_object_table")
    fun getAllAlbums(): Flow<List<AlbumObject>>

    @Query("SELECT * FROM album_object_table WHERE name LIKE '%' || :search || '%'")
    fun searchAlbums(search: String?): Flow<List<AlbumObject>>

    @Query("SELECT * FROM album_object_table WHERE albumId=:id")
    fun getAlbum(id: Int): LiveData<List<AlbumObject>>

    @Query("SELECT * FROM album_object_table WHERE albumId IN (:albumObjectIds)")
    fun loadAllAlbumsByIds(albumObjectIds: IntArray): LiveData<List<AlbumObject>>

    @Insert
    suspend fun insertAllAlbums(albumobjects: AlbumObject)

    @Delete
    suspend fun deleteAlbum(albumObject: AlbumObject)
}