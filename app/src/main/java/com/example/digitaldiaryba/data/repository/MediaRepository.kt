package com.example.digitaldiaryba.data.repository

import androidx.lifecycle.LiveData
import com.example.digitaldiaryba.data.database.MediaObjectDao
import com.example.digitaldiaryba.data.models.*
import com.example.digitaldiaryba.util.enums.EMediaType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val mediaObjectDao: MediaObjectDao
) {

    suspend fun insertImageAndCorrespondingBuildingInfoState(
        media: MediaObject,
        buildingInfoObject: BuildingInfoObject
    ) {
        buildingInfoObject.filename = media.name.toString()
        buildingInfoObject.fileUri = media.decodedUri
        mediaObjectDao.insertImageAndCorrespondingBuildingInfoState(media, buildingInfoObject)
    }

    suspend fun insertVideoAndCorrespondingBuildingInfoState(
        media: MediaObject,
        buildingInfoObject: BuildingInfoObject
    ) {
        buildingInfoObject.filename = media.name.toString()
        buildingInfoObject.fileUri = media.uri
        buildingInfoObject.mediaType = EMediaType.VIDEO
        mediaObjectDao.insertVideoAndCorrespondingBuildingInfoState(media, buildingInfoObject)
    }

    suspend fun insertMediaAndCorrespondingAudioObject(
        media: MediaObject
    ) {
        mediaObjectDao.insertMediaAndCorrespondingAudioObject(media)
    }

    suspend fun updateMedia(media: MediaObject) {
        mediaObjectDao.updateMedia(media)
    }

    fun getAllMedias(): Flow<List<MediaObject>> {
        return mediaObjectDao.getAllMedias()
    }

    fun getMediaObjectById(id: Int): Flow<MediaObject> {
        return mediaObjectDao.getMediaObject(id)
    }

    fun getMediaDecodedUri(id: Int): Flow<String> {
        return mediaObjectDao.getMediaDecodedUri(id)
    }

    fun getAllMediasFromAnAlbum(albumId: Int): Flow<List<MediaObject>> {
        return mediaObjectDao.getAllMediasFromAnAlbum(albumId)
    }

    fun getBuildingNamesForMedias(albumId: Int): Flow<List<String>> {
        return mediaObjectDao.getBuildingNamesForMedias(albumId)
    }

    fun getBuildingInfosForMedias(albumId: Int): Flow<List<BuildingInfoObject>> {
        return mediaObjectDao.getBuildingInfosForMedias(albumId)
    }

    suspend fun deleteMedia(mediaObject: MediaObject) {
        mediaObjectDao.deleteMedia(mediaObject)
    }

}