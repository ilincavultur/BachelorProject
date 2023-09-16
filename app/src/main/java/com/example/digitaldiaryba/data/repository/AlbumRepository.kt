package com.example.digitaldiaryba.data.repository

import androidx.lifecycle.LiveData
import com.example.digitaldiaryba.data.database.AlbumObjectDao
import com.example.digitaldiaryba.data.models.AlbumObject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumObjectDao: AlbumObjectDao
) {
    suspend fun insertAlbum(album: AlbumObject) {
        albumObjectDao.insertAlbum(album)
    }

    fun getAllAlbums(): Flow<List<AlbumObject>> {
        return albumObjectDao.getAllAlbums()
    }

    fun searchAlbums(search: String?): Flow<List<AlbumObject>> {
        return albumObjectDao.searchAlbums(search)
    }

    suspend fun deleteAlbum(albumObject: AlbumObject) {
        albumObjectDao.deleteAlbum(albumObject)
    }

}