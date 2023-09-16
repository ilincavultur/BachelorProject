package com.example.digitaldiaryba.data.repository

import androidx.lifecycle.LiveData
import com.example.digitaldiaryba.data.database.PresentationObjectDao
import com.example.digitaldiaryba.data.models.AlbumObject
import com.example.digitaldiaryba.data.models.BuildingInfoObject
import com.example.digitaldiaryba.data.models.MediaObject
import com.example.digitaldiaryba.data.models.PresentationObject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PresentationRepository @Inject constructor(
    private val presentationObjectDao: PresentationObjectDao
) {
    suspend fun insertPresentation(presentation: PresentationObject) {
        presentationObjectDao.insertPresentation(presentation)
    }

    fun getPresentationBuildingInfoList(id: Int): Flow<PresentationObject> {
        return presentationObjectDao.getPresentationBuildingInfoList(id)
    }

    fun getAllPresentations(): Flow<List<PresentationObject>> {
        return presentationObjectDao.getAllPresentations()
    }

    fun searchPresentations(search: String?): Flow<List<PresentationObject>> {
        return presentationObjectDao.searchPresentations(search)
    }

    suspend fun deletePresentation(presentationObject: PresentationObject) {
        presentationObjectDao.deletePresentation(presentationObject)
    }
}